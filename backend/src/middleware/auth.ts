import jwt from 'jsonwebtoken';
import {Request, Response, NextFunction} from 'express';
import User from '../models/User';
import config from '../config/config'
import logging from '../config/logging';

const NAMESPACE = "Auth";

const auth= async(req:Request,res:Response,next:NextFunction)=>{
    logging.info(NAMESPACE,`Extracting Token`);
        let token = req.headers.authorization || req.headers.authorization?.split(" ")[1]
        if(token)
        {
            jwt.verify(token,config.server.token.secret!,(err,decoded)=>{
                if(err)
                {
                    res.status(403).send({
                        "err":true,
                        "message": err.message,
                        error: err
                    })
                }
                else{
                    res.locals.jwt = decoded;
                    next();
                }
                
            })
        }
        else{
            res.status(401).send({
                "err": true,
                "message":"Please Authenticate User"
            })
        }
}

export default auth;