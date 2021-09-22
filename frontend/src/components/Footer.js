import 'bootstrap/dist/css/bootstrap.min.css';
import './Footer.css';
function Footer() {
  return (
    <>
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
    </>
  );

}
export default Footer;
