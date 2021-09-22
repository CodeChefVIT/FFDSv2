import React from "react";
import { useState } from "react";
import '../components/Login.css';
import './Moreinfo.css';
import ReactDOM from "react-dom";


function Moreinfo() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function handleSubmit(event) {
    event.preventDefault();
  }
  //Uploading Profile Pic
  const uploadedImage = React.useRef(null);
  const imageUploader = React.useRef(null);

  const handleImageUpload = e => {
    const [file] = e.target.files;
    if (file) {
      const reader = new FileReader();
      const { current } = uploadedImage;
      current.file = file;
      reader.onload = e => {
        current.src = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  };
  //Redirecting to another page after submitting

  return (
    <>
      <div className="MoreInfoTitle my-4">
        <h2 className="text-center">Tell Us About Yourself !</h2>
      </div>

      <div className="container MoreInfoProfile">
        <form method=" " onSubmit={this.submitForm.bind(this)}>
          <div className="row">
            <div className="col-md-4 mt-4 mb-3 mx-3 img-div">
              <input type="file" accept="image/*" onChange={handleImageUpload} ref={imageUploader} style={{ display: "none" }} />
              <div className="img-div" onClick={() => imageUploader.current.click()}>
                <img
                  src="https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"
                  ref={uploadedImage}
                  style={{
                    width: "100%",
                    height: "100%",
                    position: "absolute",
                  }}
                />
              </div>
            </div>

            <div className="col-md-6 mt-3">
              <div className="profile-head mt-3">
                <div className="row mb-3">
                  <div className="col">
                    <input type="text" className="form-control input-sm" placeholder="Your Name" aria-label="First name" />
                  </div>
                  <div className="col">
                    <input type="number" className="form-control input-sm" placeholder="Conatact Number" aria-label="Last name" />
                  </div>
                </div>
                <div className="row col-sm-6 mt-2">
                  <select className="form-select" aria-label="Default select example">
                    <option selected>Select Your Gender</option>
                    <option value="1">Male</option>
                    <option value="2">Female</option>
                    <option value="3">Other</option>
                  </select>
                </div>

                <div className="mb-3 mt-4 ">
                  <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Share a few lines about yourself "></textarea>
                </div>
                <div className="mb-3 mt-4 ">
                  <textarea className="form-control" id="exampleFormControlTextarea1" rows="3" placeholder="Mention Some of your Qualities "></textarea>
                </div>

                <div className="col-14 mb-4 ">
                  <button type="submit" className="btn btn-dark btn-default">Submit</button> 
                </div>

              </div>
            </div>
          </div>
        </form>
      </div>

      <div className="footer">
        <div className="row">
          <div className="footer col-lg-6">
            <div className="cl">
              <h4> Connect With Us </h4>
            </div>
          </div>

          <div class="footer-content">
            <h4>Follow us on</h4>
            <ul class="socials">
              <li><a href="https://www.facebook.com/codechefvit/"><i class="fa fa-facebook-square"></i></a></li>
              <li><a href="https://twitter.com/codechefvit?lang=en"><i class="fa fa-twitter"></i></a></li>
              <li><a href="https://www.instagram.com/codechefvit/?hl=en"><i class="fa fa-instagram"></i></a></li>
              <li><a href="https://github.com/CodeChefVIT?language=java"><i class="fa fa-github"></i></a></li>
              <li><a href="https://www.linkedin.com/company/codechef-vit-chapter/mycompany/"><i
                class="fa fa-linkedin-square"></i></a></li>
            </ul>
          </div>
          <div class="footer-bottom">
            <p> &copy; Copyright 2021 | Made With 💜 by
              <a href="http://www.codechefvit.com" target="_blank">
                <span> CodeChef-VIT</span>
              </a>
            </p>
          </div>

        </div>
      </div>
    </>
  );
}
export default Moreinfo;

