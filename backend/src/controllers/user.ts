import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';
import bcryptjs from 'bcryptjs';
import User from '../models/User';
import mongoose from 'mongoose';
import signJWT from './functions/signJWT';
import sendEmail from './functions/sendEmail';
import slotMapper from './functions/slotMapper';
import config from '../config/config';
import jwt from 'jsonwebtoken';

const NAMESPACE = 'User Controller'
const vitEmailRegex = /^([A-Za-z]+\.[A-za-z]+[0-9]{4,4}@vitstudent.ac.in)/gm;

const validateToken = (req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Token Validated, user authorized`)
    return res.status(200).json({
        message:"Authorized",
        id: res.locals.jwt.id,
        email: res.locals.jwt.email
    })
};

const verifyUser = (req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Verifying User Email`);
    const token:string = String(req.query.t);
    jwt.verify(token, config.server.token.secret, (err,decoded)=>{
        if(err)
        {
            res.status(403).send({
                "err":true,
                "message": err.message,
                "error": err
            })
        }else if(decoded){
            let {email,id}:any = decoded;
            User.findOne({email}).then(async(user:any) =>{
                console.log(user)
                if(!user){
                    return res.status(404).send(
                    {
                        err:true,
                        message: "User not found"
                    })
                }
                if(id==user._id){
                    user.verified = true;
                    await user.save();
                    return res.status(200).send({
                        message:"User Verified"
                    })
                }
            }).catch((err:any) =>{
                return res.status(500).send({
                    "err":true,
                    "error":err
                })
            })
        }
        
    })
}

const slotUploader = (req: Request, res: Response, next: NextFunction) => {
    const {slots} = req.body;

    if(slots.length===0){
        return res.status(401).send({message:"No Slots are free"})
    }

    slotMapper(slots,req,res,next);
}

const sendEmailLink = async(req: Request, res: Response, next: NextFunction) =>{
    const {email} = req.body;
    let user = await User.findOne({email});

    if(!user){
        return res.status(404).send({err:true, message:"User not found, Register First"})
    }

    if(user.verified){
        return res.status(401).send({err:true, message:"User Already Verified"})
    }

    signJWT(user,(error,token)=>{
        if(error){
            logging.error(NAMESPACE,"Unable to Sign Token: ",error)

            return res.status(401).send({
                err: true,
                message: "Not Authorized",
                "error": error
            })
        }
        else if(token){
            sendEmail(user,token,(error,message)=>{
                if(error){
                    return res.status(401).send({
                        err: true,
                        "error": error
                    })
                }else{
                    return res.status(200).send(message)
                }
            });
        }
    })


}

const login = async(req: Request, res: Response, next: NextFunction) =>{
    try{

        let {email, password} = req.body;
        const user = await User.findOne({ email })
        if(!user) return res.status(404).send({
            err: true,
            message: "User Not Found"
        })

        bcryptjs.compare(password, user.password, (error,result)=>{
            if(error){
                logging.error(NAMESPACE,error.message,error)
                return res.status(401).send({
                    err: true,
                    message: "Not Authorized",
                    "error": error
                })
            } 
            else if(result){
                signJWT(user,(error,token)=>{
                    if(error){
                        logging.error(NAMESPACE,"Unable to Sign Token: ",error)

                        return res.status(401).send({
                            err: true,
                            message: "Not Authorized",
                            "error": error
                        })
                    }
                    else if(token){
                        if(user.verified){

                            return res.status(200).json({
                                message: "Authorized",
                                token
                            })
                        }else{
                            return res.status(401).json({
                                message: "Email Not Verified",
                                token: null
                            })
                        }
                    }
                })
            }else{
                return res.status(401).json({
                    message:"User Credentials Incorrect"
                })
            }
        })
    }catch(error){
        return res.status(500).send({
            err : true,
            "error": error
        })
    }


};

const register = async(req: Request, res: Response, next: NextFunction) =>{
    try{
        logging.info(NAMESPACE,`User Create Route Called`);
        const { email, password} = req.body

        User.findOne({email}).then((user:any) =>{
            if(user){
                return res.status(401).send({
                    message:"Account Already Exists"
                })
            }
        })

        bcryptjs.hash(password,10,async(hashError, hash)=>{
            if(hashError){
                return res.status(500).json({
                    err:true,
                    message:hashError.message,
                    "error": hashError
                });
            }

        const isVit = email.match(vitEmailRegex)
        if(isVit === null){
            return res.status(400).send({
                err: true,
                message:"Enter VIT Email"
            });
        }
        else{
            let user = new User({
                _id: new mongoose.Types.ObjectId(), 
                email,
                password:hash
            })
            if(!user){
                return res.status(404).send({
                    err: true,
                    message: "User Not Registered"
                }) 
            }
            signJWT(user,(error,token)=>{
                if(error){
                    logging.error(NAMESPACE,"Unable to Sign Token: ",error)

                    return res.status(401).send({
                        err: true,
                        message: "Not Authorized",
                        "error": error
                    })
                }
                else if(token){
                    sendEmail(user,token,(error,message)=>{
                        if(error){
                            return res.status(401).send({
                                err: true,
                                "error": error
                            })
                        }else{
                            user.save()
                            return res.status(200).send(message)
                        }
                    });
                }
            })
            
        }
    })
        
    }catch(err:any){
        if(err?.code === 11000 && err.name==="MongoError"){
            return res.status(400).send({
                "err": true,
                message:"An Account already exists with that email"
            });
        }else{
            return res.status(422).send({
                "err":true,
                message:err.message
            });
        }
    }
    
    
}

const updateUser = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Update User Route Called`);
    let id = res.locals.jwt.id;
    let user = await User.findOneAndUpdate({"_id":id},req.body);
    if(!user){
        return res.status(404).send({err:true,message:"User not found"})
    }

    return res.status(200).send({
        message:"User updated successfully"
    })
    
}

const getProfile = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Get User Route Called`);
    let id = res.locals.jwt.id;
    console.log(res.locals.jwt)
    let user = await User.findOne({"_id":id})
    .select("-password")
    .catch((err:any) =>{
        return res.status(500).send({
            "err": true,
            "error": err
        })
    })
    if(!user){
        return res.status(404).send({
            "err": true,
            "message": "User not found"
        })
    }
    return res.status(200).send(user)
}

const getUser = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Get User Route Called`);
    let {id} = req.body;
    let user = await User.findOne({"_id":id})
    .select("-password")
    .catch((err:any) =>{
        return res.status(500).send({
            "err": true,
            "error": err
        })
    })
    if(!user){
        return res.status(404).send({
            "err": true,
            "message": "User not found"
        })
    }
    return res.status(200).send(user)
}

const getAllUsers = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Get All Users Route Called`);
    let users = await User.find({})
    .select('-password')
    .catch((error:any) =>{
        return res.status(400).send({
            err: true,
            "error":error
        })
    })
    if(!users) return res.status(404).send({
        err: true,
        message: "Users not found"
    })

    return res.status(200).send(users);
}

export default {getAllUsers, getProfile, slotUploader, getUser, validateToken, register, login, verifyUser, sendEmailLink, updateUser}