import jwt from 'jsonwebtoken';
import config from '../../config/config';
import logging from '../../config/logging';

const NAMESPACE = 'Auth';

const signJWT = (user:any, expirationTime:number, callback: (error: Error | null, token: string | null) => void):void =>{
    var timeSinceEpoch = new Date().getTime();
    var expiration = timeSinceEpoch + Number(expirationTime)*100000;
    var expirationTimeInSeconds = Math.floor(expiration / 1000);

    logging.info(NAMESPACE,`Attempting to sign in ${user.email}`);

    try{
        if(expirationTime!==0){
            jwt.sign(
                {
                    email: user.email,
                    id: user._id
                },
                config.server.token.secret!,
                {
                    issuer: config.server.token.issuer,
                    algorithm: 'HS256',
                    expiresIn: expirationTimeInSeconds
                },
                (error,token) =>{
                    if(error)
                    {
                        callback(error, null);
                    }else if(token){
                        callback(null, token);
                    }
                }
            )
        }else{
            jwt.sign(
                {
                    email: user.email,
                    id: user._id
                },
                config.server.token.secret!,
                {
                    issuer: config.server.token.issuer,
                    algorithm: 'HS256',
                },
                (error,token) =>{
                    if(error)
                    {
                        callback(error, null);
                    }else if(token){
                        callback(null, token);
                    }
                }
            )
        }
    }catch(error){
        logging.error(NAMESPACE,error.message,error);
        callback(error, null);
    }

};

export default signJWT;