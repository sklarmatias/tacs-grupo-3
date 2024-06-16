import React, { useState, useEffect } from 'react';
import {
    createBrowserRouter,
    RouterProvider,
    Outlet,
} from 'react-router-dom';
import { Container } from 'react-bootstrap';
import ArticleList from './articlesList.jsx';
import ArticleForm from './createArticle.jsx';
import LoginForm from './login.jsx';
import Header from './header.jsx';


function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [email, setEmail] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const storedEmail = localStorage.getItem('emailUser');
        if (token && storedEmail) {
            setIsLoggedIn(true);
            setEmail(storedEmail);
        }
    }, []);

    const handleLogin = (userEmail) => {
        setIsLoggedIn(true);
        setEmail(userEmail);
    };

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('emailUser');
        setIsLoggedIn(false);
        setEmail('');
    };
    const Layout = ({ isLoggedIn, email, onLogin, onLogout }) => (
        <>
            <Header isLoggedIn={isLoggedIn} email={email} onLogout={onLogout} />
            <Container fluid className="mt-4">
                <Outlet />
            </Container>
        </>
    );

    const router = createBrowserRouter([
        {
            path: '/',
            element: <Layout isLoggedIn={isLoggedIn} email={email} onLogin={handleLogin} onLogout={handleLogout} />,
            children: [
                {
                    path: '/',
                    element: <ArticleList userFocus="false" />,
                },
                {
                    path: 'create',
                    element: <ArticleForm />,
                },
                {
                    path: 'login',
                    element: <LoginForm onLogin={handleLogin} />,
                },
                {
                    path: 'myarticles',
                    element: <ArticleList userFocus="true" />,
                },
            ],
        },
    ]);

    return (
        <RouterProvider router={router} />
    );
}

export default App;