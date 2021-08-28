import http from 'http';
import express from 'express';
import cors from 'cors';
import mongoose from 'mongoose';
import User from './models/User';
import Conversation from './models/Conversation'
import Message from './models/Message'
import logging from './config/logging';
import config from './config/config';
import userRoutes from './routes/user';
import conversationRoutes from './routes/conversation';
import messageRoutes from './routes/message';
require('./controllers/user');

const NAMESPACE = 'Server';
const app = express();

app.use(cors());

//Logging
app.use((req,res,next) =>{
    logging.info(NAMESPACE, `METHOD - [${req.method}], URL - [${req.url}], IP - [${req.socket.remoteAddress}]`)

    res.on('finish', () =>{
        logging.info(NAMESPACE, `METHOD - [${req.method}], URL - [${req.url}], IP - [${req.socket.remoteAddress}], STATUS - [${res.statusCode}]`)
    })

    next();
});

// Parsing
app.use(express.urlencoded({
    limit: '50mb',
    parameterLimit: 100000,
    extended: true
}));
app.use(express.json({
    limit: '50mb'
}));

// Rules of API
app.use((req,res,next) => {
    res.header('Access-Control-Allow-Origin','*');
    res.header('Access-Control-Allow-Headers','Origin, X-Requested-With, Content-Type, Accept, Authorization');

    if(req.method == 'OPTIONS'){
        res.header('Access-Control-Allow-Methods', 'GET PATCH DELETE POST PUT')
        return res.status(200).json({})
    }

    next();

})

// Routes
app.use('/user', userRoutes);
app.use('/conversation', conversationRoutes);
app.use('/message', messageRoutes);

// Error Handling
app.use((req, res, next) => {
    const error = new Error("not found");

    return res.status(404).json({
        err: true,
        message: error.message
    });
});

// Database Connection
try{
    mongoose
    .connect(config.database.mongo!,config.database.options,async()=>{
        await User.init();
        await Conversation.init();
        await Message.init();
        logging.info(NAMESPACE,"Database Connected"); 
    })
}catch(err:any){
    logging.error(NAMESPACE,err.message,err);
}

// Server Creation
const httpServer = http.createServer(app);
let io = require('./socket/socket').initialize(httpServer);

app.locals.io = io
let users : any[] = [];
let usersToVerify : any[] = [];
let usersBlocked : any[] = [];

app.locals.users = usersToVerify;

const addUser = (userId:String,socketId:String) =>{
    !users.some(user=> user.userId === userId) &&
        users.push({userId,socketId});
}

const removeUser = (socketId:String) =>{
    users = users.filter(user=>user.socketId !== socketId)
}

const getUser = (userId:String) => {
    return users.find((user) => user.userId === userId);
};

const addUserVerify = (email:String,socketId:String) =>{
    !app.locals.users.some((user:any)=> user.email === email) &&
        app.locals.users.push({email,socketId});
}

const removeUserVerify = (socketId:String) =>{
    app.locals.users = app.locals.users.filter((user:any)=>user.socketId !== socketId)
}

const addUserBlocked = (userId:String, socketId:String) =>{
    !usersBlocked.some(user=> user.userId === userId) &&
    usersBlocked.push({userId,socketId});
}

const removeUserBlocked = (socketId:String) =>{
    usersBlocked = usersBlocked.filter(user=>user.socketId !== socketId)
}

const getUserBlocked = (userId:String) => {
    return usersBlocked.find((user) => user.userId === userId);
};


io.on("connection",(socket:any)=>{
    logging.info(NAMESPACE,"Connection established");

    socket.on('addUser',(userId:String)=>{
        logging.info(NAMESPACE,"User Socket Initialized",{users});
        addUser(userId, socket.id)
        io.emit("getUsers",users)
    })

    socket.on('looking',(email:String)=>{
        addUserVerify(email, socket.id)
    })

    socket.on("block", (blockerId:String, blockedId:String)=>{
        logging.info(NAMESPACE,"Terminating Socket Messaging on Block");
        addUserBlocked(blockerId, socket.id);
        const user = getUser(blockedId);
        io.to(user.socketId).emit("getBlocked", {
            "blocked":true
        });
    })

    socket.on("sendMessage", (senderId:String, receiverId:String, text:String, createdAt:Date) => {
        logging.info(NAMESPACE,"Sending a Message",{senderId,receiverId,text,createdAt});
        const user = getUser(receiverId);
        const hasBlocked = getUserBlocked(receiverId);
        if(hasBlocked === null || hasBlocked === undefined){
            io.to(user.socketId).emit("getMessage", {
              senderId,
              text,
              createdAt
            });
        }
      });

    socket.on("disconnect",()=>{
        logging.info(NAMESPACE,"A User Disconnected");
        removeUser(socket.id);
        removeUserVerify(socket.id);
        removeUserBlocked(socket.id)
        io.emit("getUsers",users)
    })
})


httpServer.listen(config.server.port, ()=>{
    logging.info(NAMESPACE, `Server running on http://${config.server.hostname}:${config.server.port}/`);
    
})


