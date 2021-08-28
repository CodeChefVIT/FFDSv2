import {Request, Response, NextFunction} from 'express';
import jwt from 'jsonwebtoken';
import logging from '../config/logging';
import bcryptjs from 'bcryptjs';
import User from '../models/User';
import mongoose from 'mongoose';
import signJWT from './functions/signJWT';
import sendEmail from './functions/sendEmail';
import slotMapper from '../services/slotMapper';
import upload from '../services/imageUpload';
import imageDelete from '../services/imageDelete';
import config from '../config/config';

interface MulterRequest extends Request {
    file: any;
}

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

const getFeed = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Get User Matching Feed`);
    try{
        let id = res.locals.jwt.id;
        let final: any = [];
        let user = await User.findOne({"_id":id})
        .catch((err:any) =>{
            return res.status(500).send({
                "err": true,
                "error": err
            })
        })
        let feed = [];
        if(user.genderPreference!=="none"){
            feed = await User.find({"_id":{ $nin: user.rejected.concat(user.accepted).concat(user.blocked).concat([id]) }, "gender": user.genderPreference, "verified":true}).select("-password")
        }else{
            feed = await User.find({"_id":{ $nin: user.rejected.concat(user.accepted).concat(user.blocked).concat([id]) }, "verified":true}).select("-password")
        }
        if(!feed || feed === []){
            return res.status(404).send({"message":"No Matches Found"});
        }
        if(user.slot === []){
            return res.status(400).send({"message":"User TimeTable is Not Uploaded"});
        }
        feed.forEach((match:any) => {
            if(match.slot !== []){
                let commonSlotLength = 0;
                let tempUser = match._doc;
                for(let i=0; i<7; i++) {
                    for(let j=0; j<match.slot[i].length; j++) {
                        if(match.slot[i][j].free && user.slot[i][j].free){
                            commonSlotLength++;
                        }
                    }
                }
                delete tempUser['slot'];
                final.push({...tempUser, commonSlotLength});
            }
        })

        final.sort(function(a:any, b:any) {
            var keyA = new Date(a.commonSlotLength),
              keyB = new Date(b.commonSlotLength);
            if (keyA > keyB) return -1;
            if (keyA < keyB) return 1;
            return 0;
          });
        
        if(final === []){
            return res.status(404).send({"message":"No Matches Found"});
        }

        return res.status(200).send({"feed": final});

    }catch(err){
        return res.status(500).send({
            err: true,
            "error":err
        })
    }
}

const verifyUser = (req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Verifying User Email`);
    const io = req.app.locals.io;
    const getUserVerify = (email:String) => {
        return req.app.locals.users.find((user:any) => user.email === email);
    };
    const removeUserVerify = (socketId:String) =>{
        req.app.locals.users = req.app.locals.users.filter((user:any)=>user.socketId !== socketId)
    }
    const token:string = String(req.query.t);
    jwt.verify(token, config.server.token.secret!, (err,decoded)=>{
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
                    let verifiedUser = await getUserVerify(user.email);
                    if(verifiedUser){
                        io.to(verifiedUser.socketId).emit("verified",{verified: true});
                        removeUserVerify(verifiedUser.socketId);
                    }
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
    const {Slots} = req.body;

    if(Slots.length===0){
        return res.status(401).send({message:"No Slots are free"})
    }

    slotMapper(Slots,req,res,next);
}

const sendEmailVerification = async(req: Request, res: Response, next: NextFunction) =>{
    const {email} = req.body;
    let user = await User.findOne({email});

    if(!user){
        return res.status(404).send({err:true, message:"User not found, Register First"})
    }

    if(user.verified){
        return res.status(401).send({err:true, message:"User Already Verified"})
    }

    signJWT(user, config.server.token.expireTimeDay, (error,token)=>{
        if(error){
            logging.error(NAMESPACE,"Unable to Sign Token: ",error)

            return res.status(401).send({
                err: true,
                message: "Not Authorized",
                "error": error
            })
        }
        else if(token){
            sendEmail.verification(user,token,(error:any,message:any)=>{
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

const sendEmailPassword = async(req: Request, res: Response, next: NextFunction) =>{
    const {email} = req.body;
    let user = await User.findOne({email});

    if(!user){
        return res.status(404).send({err:true, message:"User not found, Register First"})
    }

    signJWT(user, config.server.token.expireTimeDay, (error,token)=>{
        if(error){
            logging.error(NAMESPACE,"Unable to Sign Token: ",error)

            return res.status(401).send({
                err: true,
                message: "Not Authorized",
                "error": error
            })
        }
        else if(token){
            sendEmail.password(user,token,(error:any,message:any)=>{
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

const passwordReset = (req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Password Reset Route Called`);
    const token:string = String(req.query.t);
    jwt.verify(token, config.server.token.secret!, (err,decoded)=>{
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
                if(!user){
                    return res.status(404).send(
                    {
                        err:true,
                        message: "User not found"
                    })
                }
                if(id==user._id){
                    return res.status(200).send({
                        redirect: true,
                        userId: id,
                        message:"User Verified, Redirect to password reset"
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

const updatePassword =  async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Resetting Password Route Called`);
    const {id, password} = req.body;
    bcryptjs.hash(password,10,async(hashError, hash)=>{
        if(hashError){
            return res.status(500).json({
                err:true,
                message:hashError.message,
                "error": hashError
            });
        }
        let user = await User.findOneAndUpdate(id,{password: hash});
        if(user===null || user===undefined){
            return res.status(404).json({err:true,message:"User not found"})
        }
        else{
            return res.status(200).json({message:"Password Resetted Successfully"});
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
                signJWT(user, config.server.token.expireTimeLong, (error,token)=>{
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
            signJWT(user, config.server.token.expireTimeDay, (error,token)=>{
                if(error){
                    logging.error(NAMESPACE,"Unable to Sign Token: ",error)

                    return res.status(401).send({
                        err: true,
                        message: "Not Authorized",
                        "error": error
                    })
                }
                else if(token){
                    sendEmail.verification(user,token,(error:any,message:any)=>{
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
    let body = req.body;
    let id = res.locals.jwt.id;
    if(body['_id']){
        delete body['_id'];
    }
    let user = await User.findOneAndUpdate({"_id":id},body);
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
    let id = req.query.id;
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

const rejectMatch = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Rejecting a match`);
    let selfUserId = res.locals.jwt.id;
    try {
    const selfUser = await User.findById(selfUserId);
    if(selfUser.rejected.includes(req.params.userId)) return res.status(400).send({err:true,message:"User already rejected"})
    else if(selfUser.accepted.includes(req.params.userId)) return res.status(400).send({err:true,message:"User already accepted"})
    else if(selfUser.blocked.includes(req.params.userId)) return res.status(400).send({err:true,message:"User already blocked"})
    else{
        selfUser.rejected.push(req.params.userId);
        selfUser.save();
        return res.status(200).send({message:"User successfully rejected"})
    }

    } catch (err) {
        return res.status(500).json(err);
      }
}

const imageUpload = async(req: any , res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Uploading an image`);
    let id = res.locals.jwt.id;
    let user = await User.findById(id).catch((err:any)=>{
        return res.status(500).json(err);
    })
    console.log(user)
    if(user.userImage.key!=="" || user.userImage.url!==""){
        imageDelete(user.userImage.key, req, res, next);
    }
    const singleUpload = upload(res).single('image');
    singleUpload(req, res, function(err:any) {
        if (err) {
          return res.status(422).send({errors: [{title: 'Image Upload Error', detail: err.message}]});
        }
        try{
            user.userImage.key = req.file.key;
            user.userImage.url = req.file.location;
            user.save();
            return res.status(200).json({...user.userImage, message:"Image Uploaded Succesfully"});
        }catch(err:any){
            return res.status(500).send(err);
        }
      });
}

export default {imageUpload, getAllUsers, getProfile, slotUploader, getUser, validateToken, register, login, verifyUser, sendEmailVerification, sendEmailPassword, passwordReset, updatePassword, updateUser, getFeed, rejectMatch}