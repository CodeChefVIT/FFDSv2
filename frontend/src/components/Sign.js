import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import './Sign.css';

 function Signup() {
   
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmpassword, setConfirmPassword]= useState("");
    function validateForm() {
      return email.length > 0 && password.length > 0;
    }
    
    function handleSubmit(event) {
      event.preventDefault();
      if (password===confirmpassword)
      {
        console.log("success")
      
      }
      else{
        console.log("failure")
      }
     
    }
  
    return (
      <>
      <div>
        <h3>HOLLA!<br/>
        welcome to ffds

        </h3>
        <h4>Please enter the required fields:</h4>
      </div>
      
      <div className="signup">
        <Form onSubmit={handleSubmit}>
          <Form.Group size="lg" controlId="email">
            <Form.Label>Email</Form.Label>
            <Form.Control className="input"
              autoFocus
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </Form.Group>
          <Form.Group size="lg" controlId="password">
            <Form.Label>Password</Form.Label>
            <Form.Control className="input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </Form.Group>
          <Form.Group size="lg" controlId="password">
            <Form.Label> confirm Password</Form.Label>
            <Form.Control className="input"
              type="password"
              value={confirmpassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
          </Form.Group>
          <Button className="loginbutton" block size="lg" type="submit"  disabled={!validateForm()}>
            Signup
          </Button>
        </Form>
      </div>
      </>
    );
  }
  export default Signup;
 
