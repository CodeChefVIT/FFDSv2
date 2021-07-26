import mongoose, { Schema } from 'mongoose';

const MessageSchema: Schema = new Schema({
        conversationId:{
            type: String,
            required:true
        },
        senderId: {
            type: String,
            required:true
        },
        text: {
            type: String,
            required:true
        },
        createdAt:{
            type: Date,
            required:true
        },
        updatedAt:{
            type: Date,
            required:true
        }
    },
)

var messageModel = mongoose.model("Message", MessageSchema);

export default messageModel;