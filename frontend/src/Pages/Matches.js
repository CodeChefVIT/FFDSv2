import React from "react";
import Carousel from "react-bootstrap/Carousel";
import { Modal,Button } from "react-bootstrap";
import "./Matches.css";
function MyVerticallyCenteredModal(props) {
    return (
      <Modal 
        {...props}
        size="lg"
        aria-labelledby="contained-modal-title-vcenter"
        centered
      >
        <Modal.Header closeButton>
          <Modal.Title id="contained-modal-title-vcenter">
            Modal heading
          </Modal.Title>
        </Modal.Header>
        <Modal.Body >
          <h4>Centered Modal</h4>
          <p>
            Lorem ipsum dolor sit amet consectetur adipisicing elit. Voluptatum ex optio obcaecati, officiis deserunt modi aperiam accusamus accusantium soluta totam vitae similique sapiente nihil vel inventore eum in, sit animi ut delectus esse voluptas excepturi neque sunt? Laborum sit nostrum temporibus laudantium impedit doloremque optio tempora possimus ut, est quaerat recusandae quibusdam dignissimos provident, explicabo repellendus dolore? Ex velit, possimus quis quam temporibus doloremque nobis. Labore neque inventore iusto veniam, dolorum ipsa nihil facilis quod nobis? Error cupiditate sit similique tenetur fugit distinctio fuga eveniet eligendi porro voluptas, enim, maiores vero hic, magnam rerum placeat dolor. Facere, inventore. Sequi, qui!
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button onClick={props.onHide} variant="success">Accept </Button>
          <Button onClick={props.onHide} variant="danger">Reject</Button>
        </Modal.Footer>
      </Modal>
    );
  }
const Matches = () => {
    const [modalShow, setModalShow] = React.useState(false);
  return (
    <div className="container">
      <Carousel>
        <Carousel.Item  onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="https://source.unsplash.com/WLUHO9A_xik/500x500"
            alt="First slide"
          />
          
        </Carousel.Item >
        <Carousel.Item onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="https://source.unsplash.com/user/erondu/500x500"
            alt="Second slide"
          />

          
        </Carousel.Item>
        <Carousel.Item onClick={() => setModalShow(true)}>
          <img
            className="d-block w-100"
            src="https://source.unsplash.com/user/erondu/500x500"
            alt="Third slide"
          />
 
        </Carousel.Item>
      </Carousel>
      <MyVerticallyCenteredModal
        show={modalShow}
        onHide={() => setModalShow(false)}
      />
    </div>
    
  );
};

export default Matches;
