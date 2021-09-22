
import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import './Login.css';
// import { AiFillEyeInvisible } from "react-icons/ai";
import VisibilityIcon from '@material-ui/icons/Visibility';
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff';

function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showpassword, setShowpassword] = useState(false);

    function validateForm() {
        return email.length > 0 && password.length > 0;
    }

    function handleSubmit(event) {
        event.preventDefault();
        
        
    }

    return (
        <>
        <div className="row m-0">
        <div className="col-7 px-0" >

        <div className="message">
        <h3 >Hey!<br/>Welcome Back</h3>
            
            
            <h4>Please enter the required fields:</h4>
            </div>
            
        


        
        <div className="Login">
            <Form onSubmit={handleSubmit}>
                <Form.Group size="lg" controlId="email">
                    <Form.Label style={{ padding: "0px 0px 20px 0px" }}>Email your VIT email address:</Form.Label>
                    <Form.Control style={{ border: "0px 5px" }} className="input"
                        autoFocus
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                </Form.Group>
                <Form.Group size="lg" controlId="password">
                    <Form.Label style={{ padding: "0px 0px 20px 0px" }}>Password:</Form.Label> 
                     
          {showpassword
          ? <VisibilityIcon className="passicon" style={{color: ""}} onClick={() => setShowpassword(!showpassword)} />
          : <VisibilityOffIcon className="passicon" style={{color: ""}}  onClick={() => setShowpassword(!showpassword)} />
          }
        
                    <Form.Control className="input"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        
                    />
                   

                </Form.Group >

                <Button className="loginbutton" block size="lg" type="submit" disabled={!validateForm()}>
                    Login
                </Button>
            </Form>
        </div>
        </div>

        </div>
        </>

    );
}
export default Login;
 
