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
import SubscribersList from './subscribersList.jsx';
import PrivateRoute from './privateroute.jsx';
import Reports from './reports.jsx';


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
            <div className="header-container">
                <Header isLoggedIn={isLoggedIn} email={email} onLogout={onLogout} />
            </div>
            <div className="content-container">
                <Container fluid className="mt-5">
                    <Outlet />
                </Container>
            </div>
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
                    element: <PrivateRoute />,
                    children: [
                        {
                            path: '',
                            element: <ArticleForm />,
                        },
                    ],
                },
                {
                    path: 'login',
                    element: <LoginForm onLogin={handleLogin} />,
                },
                {
                    path: 'myarticles',
                    element: <PrivateRoute />,
                    children: [
                        {
                            path: '',
                            element: <ArticleList userFocus="true" />,
                        },
                    ],
                },
                {
                    path: 'register',
                    element: <LoginForm isRegister={true} />,
                },
                {
                    path: 'subscribers/:articleId',
                    element: <PrivateRoute />,
                    children: [
                        {
                            path: '',
                            element: <SubscribersList />,
                        },
                    ],
                },
                {
                    path: 'reports',
                    element: <PrivateRoute />,
                    children: [
                        {
                            path: '',
                            element: <Reports />,
                        },
                    ],
                },
            ],
        },
    ]);

    return (
        <RouterProvider router={router} />
    );
}

export default App;