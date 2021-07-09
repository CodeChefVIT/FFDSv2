import http from 'http';
import express from 'express';
import cors from 'cors';
import mongoose from 'mongoose';
import User from './models/User'
import logging from './config/logging';
import config from './config/config';
import userRoutes from './routes/user';
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
    .connect(config.database.mongo,config.database.options,async()=>{
        await User.init();
        logging.info(NAMESPACE,"Database Connected"); 
    })
}catch(err:any){
    logging.error(NAMESPACE,err.message,err);
}

// Server Creation
const httpServer = http.createServer(app);
httpServer.listen(config.server.port, ()=>{
    logging.info(NAMESPACE, `Server running on ${config.server.hostname}:${config.server.port}`);
    
})