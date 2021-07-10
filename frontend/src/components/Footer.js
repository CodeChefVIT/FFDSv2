import 'bootstrap/dist/css/bootstrap.min.css';
import './Footer.css';
function Footer(){
    return(
      <div className="row">
        <div className="footer col-lg-6">
        <h1> ~Made with Love by Codechef-VIT</h1>
        </div>
        
        
        {/* <div class="container" id="footermsg"> */}
         
     
          <div class="social-icons col-lg-6">
          <h1>Follow us on</h1>
            <a href="/#"><img src="images/facebookfigma.png " alt ="blah" /></a>
           
            <a href="https://www.instagram.com/codechefvit//"><img src="images/instafigma.png" alt="blah" /></a>
           
           
          {/* </div> */}
        </div>
      
      </div>
    
      ); 
    
}
export default Footer;
