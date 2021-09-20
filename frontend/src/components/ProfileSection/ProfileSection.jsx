import "./ProfileSection.css";
import logo from "../LandingSection/ffdslogo.svg";
import home from "../LandingSection/homelogo.svg";
import insta from "../LandingSection/2.svg";
import fb from "../LandingSection/3.svg";

const ProfileSection = () => {
  return (
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
      <main className="faq">
        <article className="faq-main">
          <h2 className="faq-content">
            <span class="q-head">QnA</span>
            <br></br>
            <br></br>
            Q. Lorem ipsum dolor sit amet.<br></br>
            A. Lorem ipsum dolor sit amet.<br></br>
            Q. Lorem ipsum dolor sit amet.<br></br>
            A. Lorem ipsum dolor sit amet.
          </h2>
        </article>
      </main>
      <footer>
        <div class="footer-content">Made with &#10084; by Codechef-VIT</div>
        <div class="social">
          Follow us on<br></br>
          <img src={insta} class="social-ico"></img>
          <img src={fb} class="social-ico"></img>
        </div>
      </footer>
    </section>
  );
};

export default ProfileSection;
