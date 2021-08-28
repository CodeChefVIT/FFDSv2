import aws from 'aws-sdk';
import multerS3 from 'multer-s3';
import multer from 'multer';
import config from '../config/config';
import {Request, Response} from 'express';

aws.config.update({
  accessKeyId: config.aws.id!,
  secretAccessKey: config.aws.key!,
});

const s3 = new aws.S3();

const fileFilter = (req: Request, file:any, cb:any) => {
  if (file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
    cb(null, true);
  } else {
    cb(new Error('Invalid file type, only JPEG and PNG are allowed'), false);
  }
}

const upload = (res:Response) => multer({
  fileFilter,
  storage: multerS3({
    acl: 'public-read',
    s3,
    contentType: multerS3.AUTO_CONTENT_TYPE,
    bucket: 'ffds-cc',
    metadata: function (req, file, cb) {
      cb(null, {profileImage: 'FFDS Profile Image'});
    },
    key: (req, file, cb) => {
      if(file.mimetype==='image/jpeg'){
        cb(null, "files_from_node/ffds-"+res.locals.jwt.id+".jpeg")
      }else if(file.mimetype==='image/png'){
        cb(null, "files_from_node/ffds-"+res.locals.jwt.id+".png")
      }
    }
  })
});

export default upload;

// (req:Request, res:Response, file:Express.Multer.File, cb(error: any, key?: string | undefined) => void):void