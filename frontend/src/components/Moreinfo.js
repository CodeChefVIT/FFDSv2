import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import './Login.css';

 function Moreinfo() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
  
    function validateForm() {
      return email.length > 0 && password.length > 0;
    }
  
    function handleSubmit(event) {
      event.preventDefault();
    }
  
    return (
     
     
      <div className="moreinfo">
        <Form onSubmit={handleSubmit}>
          <Form.Group size="lg" controlId="email">
            <Form.Label style={{ padding:"0px 0px 20px 0px" }}>Enter your full name*</Form.Label>
            <Form.Control style={{ border:"0px 5px" }} className ="input"
              autoFocus
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </Form.Group>
          <Form.Group size="lg" controlId="number">
            <Form.Label  style={{ padding:"0px 0px 20px 0px" }}>Enter your phone number*</Form.Label>
            <Form.Control className="input"
              type="number"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </Form.Group>
          <Button className= "loginbutton" block size="lg" type="submit" disabled={!validateForm()}>
            Login
          </Button>
        </Form>
      </div>
     
    );
  }
  export default Moreinfo;
 
