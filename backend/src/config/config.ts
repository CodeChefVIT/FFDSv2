import dotenv from "dotenv";
import process from "process";
dotenv.config();

const SERVER_HOSTNAME = process.env.SERVER_HOSTNAME || 'localhost';
const SERVER_PORT = process.env.PORT || 4000;
const SERVER_TOKEN_EXPIRETIME_LONG = 0;
const SERVER_TOKEN_EXPIRETIME_DAY = 864;
const SERVER_TOKEN_ISSUER = process.env.ISSUER || "CodeChef-VIT";
const SERVER_TOKEN_SECRET = process.env.SECRET; 
const DATABASE_URL =  process.env.DB_CONNECT;
const MAIL_USERNAME = process.env.MAIL_USERNAME;
const MAIL_PASSWORD = process.env.MAIL_PASSWORD;
const ACCESS_KEY_ID = process.env.AWS_ACCESS_KEY_ID;
const SECRET_ACCESS_KEY = process.env.AWS_SECRET_ACCESS_KEY;

const DATABASE_OPTIONS = {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useCreateIndex: true,
    useFindAndModify: false
}

const SERVER = {
    hostname: SERVER_HOSTNAME,
    port: SERVER_PORT,
    token:{
        expireTimeLong: SERVER_TOKEN_EXPIRETIME_LONG,
        expireTimeDay: SERVER_TOKEN_EXPIRETIME_DAY,
        issuer: SERVER_TOKEN_ISSUER,
        secret: SERVER_TOKEN_SECRET
    }
}

const DATABASE = {
    mongo : DATABASE_URL,
    options: DATABASE_OPTIONS
}

const AWS = {
    id : ACCESS_KEY_ID,
    key : SECRET_ACCESS_KEY
}

const MAIL = {
    user: MAIL_USERNAME,
    pass: MAIL_PASSWORD
}

const config = {
    server: SERVER,
    database: DATABASE,
    mail: MAIL,
    aws: AWS
}

export default config;
