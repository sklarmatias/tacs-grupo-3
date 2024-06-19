import React from 'react';
import { Navbar, Nav, Button, Container, NavDropdown } from 'react-bootstrap';
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
            <Container>
            <Navbar bg="light" expand="lg" sticky="top">
            <Navbar.Brand as={Link} to="/">HOME</Navbar.Brand>
                <NavDropdown title="Links" id="basic-nav-dropdown">
                    <NavDropdown.Item href="/">Listado Articulos</NavDropdown.Item>
                    {isLoggedIn && (
                        <>
                            <NavDropdown.Item href="/myarticles">Mis Articulos</NavDropdown.Item>
                            <NavDropdown.Item href="/create">Publicar Articulos</NavDropdown.Item>
                            <NavDropdown.Item href="/reports">Reportes</NavDropdown.Item>
                        </>
                    )}
                </NavDropdown>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse className="justify-content-end">
                    {isLoggedIn ? (
                        <>
                            <Navbar.Text>{email}</Navbar.Text>
                            <Button variant="outline-danger" onClick={handleLogout}>Logout</Button>
                        </>
                    ) : (
                        <>
                                <Button variant="outline-primary" as={Link} to="/login">Login</Button>
                                <Button variant="outline-primary" as={Link} to="/register">Register</Button>
                            </>
                    )}
                </Navbar.Collapse>
        </Navbar>
            </Container>
    );
};

export default Header;