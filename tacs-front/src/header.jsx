import React from 'react';
import { Navbar, Nav, Button, Container } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';

const Header = ({ isLoggedIn, email, onLogout }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('emailUser');
        onLogout();
        navigate('/'); // Redirect to home after logout
    };

    return (
        <Navbar bg="light" expand="lg" sticky="top">
            <Container fluid>
            <Navbar.Brand as={Link} to="/">MyApp</Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse className="justify-content-end">
                <Nav>
                    {isLoggedIn ? (
                        <>
                            <Nav.Item className="d-flex align-items-center">
                                <span className="me-3">{email}</span>
                            </Nav.Item>
                            <Nav.Item>
                                <Button variant="outline-danger" onClick={handleLogout}>Logout</Button>
                            </Nav.Item>
                        </>
                    ) : (
                        <Nav.Item>
                            <Button variant="outline-primary" as={Link} to="/login">Login</Button>
                        </Nav.Item>
                    )}
                </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;