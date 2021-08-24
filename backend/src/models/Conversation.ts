import mongoose, { Schema } from 'mongoose';

const ConversationSchema: Schema = new Schema(
  {
    members: {
      type: Array,
      required:true
    },
    matched:{
      type: Boolean,
      default:false
    },
    blocked:{
      type: Boolean,
      default:false
    },
    hasMessages:{
      type: Boolean,
      default:false
    },
  },
  { timestamps: true }
);

var conversationModel = mongoose.model("Conversation", ConversationSchema);

export default conversationModel;