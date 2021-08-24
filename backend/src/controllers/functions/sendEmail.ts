import nodemailer from 'nodemailer';
import config from '../../config/config';
import logging from '../../config/logging';

let transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
      user: config.mail.user!,
      pass: config.mail.pass!,
    }
  });

const NAMESPACE = 'Mailer';


const verification = (user:any, token:string, callback:(error: Error | null, message: any | null) => void): void => {
    logging.info(NAMESPACE,`Sending a verification E-Mail`)
    const url = `https://ffds-backend.herokuapp.com/user/verification?t=${token}`
    let mailOptions={
        to : user.email,
        subject : "FFDS: Please confirm your Email account",
        html : "Hello,<br> Please Click on the link to verify your email.<br><a href="+url+">Click here to verify</a>"
    }
    transporter.sendMail(mailOptions, function(error, response){
        if(error){
            callback(error,null)
        }else{
            callback(error,{
                message:"Email Sent for account verification"
            })
            }
        });
}

const password = (user:any, token:string, callback:(error: Error | null, message: any | null) => void): void => {
    logging.info(NAMESPACE,`Sending a password reset E-Mail`)
    const url = `https://ffds-backend.herokuapp.com/user/passwordReset?t=${token}`
    let mailOptions={
        to : user.email,
        subject : "FFDS: Password Reset",
        html : "<br> Please Click on the link to reset your password.<br><a href="+url+">Click here</a><br>If you think this was not you, ignore this email"
    }
    transporter.sendMail(mailOptions, function(error, response){
        if(error){
            callback(error,null)
        }else{
            callback(error,{
                message:"Password Reset Email Sent"
            })
            }
        });
}

export default {verification,password};