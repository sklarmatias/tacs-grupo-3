import React from 'react';
import { Navbar, Nav, Button, Container, NavDropdown } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useTranslation } from 'react-i18next';

const Header = ({ isLoggedIn, email, onLogout }) => {
    const { t } = useTranslation();
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
                <Navbar.Brand as={Link} to="/">{t('navbar.home')}</Navbar.Brand>
                <NavDropdown title={t('navbar.links')} id="basic-nav-dropdown">
                    <NavDropdown.Item as={Link} to="/">{t('navbar.listArticles')}</NavDropdown.Item>
                    {isLoggedIn && (
                        <>
                            <NavDropdown.Item as={Link} to="/myarticles">{t('navbar.myArticles')}</NavDropdown.Item>
                            <NavDropdown.Item as={Link} to="/create">{t('navbar.publishArticles')}</NavDropdown.Item>
                            <NavDropdown.Item as={Link} to="/reports">{t('navbar.reports')}</NavDropdown.Item>
                            <NavDropdown.Item as={Link} to="/sessions">{t('navbar.sessions')}</NavDropdown.Item>
                        </>
                    )}
                </NavDropdown>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse className="justify-content-end">
                    {isLoggedIn ? (
                        <>
                            <Navbar.Text>{email}</Navbar.Text>
                            <Button variant="outline-danger" onClick={handleLogout}>{t('navbar.logout')}</Button>
                        </>
                    ) : (
                        <>
                            <Button variant="outline-primary" as={Link} to="/login">{t('navbar.login')}</Button>
                            <Button variant="outline-primary" as={Link} to="/register">{t('navbar.register')}</Button>
                        </>
                    )}
                </Navbar.Collapse>
            </Navbar>
        </Container>
    );
};

export default Header;