import dotenv from "dotenv";
import process from "process";
dotenv.config();

const SERVER_HOSTNAME = process.env.SERVER_HOSTNAME || 'localhost';
const SERVER_PORT = process.env.PORT || 4000;
const SERVER_TOKEN_EXPIRETIME = process.env.EXPIRETIME || 3600;
const SERVER_TOKEN_ISSUER = process.env.ISSUER || "JustAnIssuer";
const SERVER_TOKEN_SECRET = process.env.SECRET || "GoodLuckWithTheSecret"; 
const DATABASE_URL =  process.env.DB_CONNECT || "URI";
const MAIL_USERNAME = process.env.MAIL_USERNAME || "username";
const MAIL_PASSWORD = process.env.MAIL_PASSWORD || "password";
interface mail {
    user: string;
    pass: string;
}

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
        expireTime: SERVER_TOKEN_EXPIRETIME,
        issuer: SERVER_TOKEN_ISSUER,
        secret: SERVER_TOKEN_SECRET
    }
}

const DATABASE = {
    mongo : DATABASE_URL,
    options: DATABASE_OPTIONS
}

const MAIL: mail = {
    user: MAIL_USERNAME,
    pass: MAIL_PASSWORD
}

const config = {
    server: SERVER,
    database: DATABASE,
    mail: MAIL
}

export default config;
