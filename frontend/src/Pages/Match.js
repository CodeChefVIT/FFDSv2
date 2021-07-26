import React from 'react'
import "./Match.css"
 const Match = () => {
    
    return (
        <div>

            <h4 style={{color: "white"}}> MATCHES</h4>
            <>
            <a href="/#" className="circle"><img src="images/circle.jpg" alt ="blah" style={{width:"200px" , height:"200px" ,borderRadius:"200px",marginRight:"50px" ,marginTop:"50px",marginBottom:"50px"}} /></a>
            <a href="/#" className="circle"><img src="images/circle.jpg" alt ="blah" style={{width:"200px" , height:"200px" ,borderRadius:"200px",marginRight:"50px" ,marginTop:"50px",marginBottom:"50px" }} /></a>
            <h4 style={{color: " white"}}>Messages</h4>
            <a href="/#" className="circle"><img src="images/circle.jpg" alt ="blah" style={{width:"200px" , height:"200px" ,borderRadius:"200px" ,marginRight:"50px" ,marginTop:"50px"}} /></a>
            </>
        </div>
    );
}
export default Match;