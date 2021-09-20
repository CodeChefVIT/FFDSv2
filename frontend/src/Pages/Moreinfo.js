import React, { useState } from "react";
import Form from "react-bootstrap/Form";
import Button from "react-bootstrap/Button";
import '../components/Login.css';
import './Moreinfo.css';
import '../components/ProfileSection/ProfileSection.css';

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
     <>
      <section id="profile">
      
      <main className="frm">
        <div class="pfp">
          <img src="http:\\placehold.it\1000x1000" class="pfpimg"></img>
        </div>
        <div className="pfpdetails">
          <span class="pink">Your Name</span>
          +91 1023456789
        </div>
        <div class="frmmain">
          <span class="pink">About me</span>
          <input type="text" class="input"></input>
          <span class="pink">Expectations</span>
          <input type="text" class="input"></input>
          <span class="pink">Gender</span>
          <label class="container opt1">
            Male
            <input type="radio" name="radio"></input>
            <span class="checkmark"></span>
          </label>
          <label class="container">
            Female
            <input type="radio" name="radio"></input>
            <span class="checkmark"></span>
          </label>
          <label class="container">
            Other
            <input type="radio" name="radio"></input>
            <span class="checkmark"></span>
          </label>
          <span class="pink">Timetable</span>
          <input type="file" class="file"></input>
        </div>
      </main>
    </section>

     <div className="infomsg">
      <h3> Tell us about you</h3>
      </div>
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
          
          <Button className="moreinfobutton" block size="lg" type="submit" disabled={!validateForm()}>
            Submit
          </Button>
        </Form>
      </div>
      </>
     
    );
  }
  export default Moreinfo;
 
 