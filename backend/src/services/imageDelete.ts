import aws from 'aws-sdk';
import multerS3 from 'multer-s3';
import multer from 'multer';
import config from '../config/config';
import logging from '../config/logging';
import {Request, Response, NextFunction} from 'express';

aws.config.update({
    accessKeyId: config.aws.id!,
    secretAccessKey: config.aws.key!,
  });
  
  const s3 = new aws.S3();

const NAMESPACE = "Image Delete"

const deleteImage = (key: string , req:Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,`Deleting an image`);
    let params = {  Bucket: 'ffds-cc', Key: key };

    s3.deleteObject(params, function(err, data) {
        if (err) return res.status(500).send(err);  // error
        else logging.info(NAMESPACE,`Image Deleted for user ${res.locals.jwt.email}`);                 // deleted
      });
}

export default deleteImage;