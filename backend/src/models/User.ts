import mongoose, { Schema } from 'mongoose';

const UserSchema: Schema = new Schema({
    name:{
        type: String,
        trim: true,
        default:""
    },
    email:{
        type:String,
        unique:[true, "Account with this email already exists"],
        required: true,
        trim:true,
    },
    genderPreference:{
        type: String,
        default:"none"
    },
    accepted:[
        {
            type: String,
            default: ""
        }
    ],
    rejected:[
        {
            type: String,
            default: ""
        }
    ],
    blocked:[
        {
            type: String,
            default: ""
        }
    ],
    verified:{
        type:Boolean,
        default:false
    },
    password: {
        type: String,
        required: true
    },
    phone:{
        type:String,
        default:""
    },
    branch:{
        type:String,
        default:""
    },
    gender:{
        type:String,
        default:""
    },
    bio: {
        type: String,
        default:""
    },
    year:
    {
        type:String,
        default:""
    },
    expectations:[
        {
            type: String,
            default: ""
        }
    ],
    slot:
    [
        [
            {
                type: Object,
                required: true
            }
        ]
    ],
    userImage:{
        key:{
            type:String,
            default:""
        },
        url:{
            type:String,
            default:""
        }
    }
},
{
    timestamps:true
})

var userModel = mongoose.model('Users', UserSchema);

export default userModel;