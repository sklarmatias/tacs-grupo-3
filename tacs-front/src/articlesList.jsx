import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Stack, Table, Button } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

function ArticleList({ userFocus, onLogout }) {
    const { t } = useTranslation();
    const [articles, setArticles] = useState([]);
    const [isLoggedIn, setIsLoggedIn] = useState(false); // State to track login status
    const [isUserFocus, setIsUserFocus] = useState(false);
    const [user, setUser] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const fetchArticles = () => {
            const headers = userFocus === 'true' ? { 'session': token } : {};
            axios.get(`${import.meta.env.VITE_API_URL}/articles`, { headers })
                .then(response => {
                    setArticles(response.data);
                })
                .catch(async error => {
                    if (error.response && error.response.status === 401) {
                        alert(t('loggedout'));
                        handleLogout();
                    }
                    else {
                        alert(`${t('articles.errorFetchingArticles')} ${error}`);
                    }
                    
                });

            setIsUserFocus(userFocus === 'true');
        };

        fetchArticles();

        if (token) {
            setIsLoggedIn(true);
            setUser(token);
        }
        if (userFocus == 'true') {
            setIsUserFocus(true);
        }
        else {
            setIsUserFocus(false);
        }
    }, [userFocus, t]);
    

    const subscribe = (id) => {
        axios.post(`${import.meta.env.VITE_API_URL}/articles/${id}/users/`, '', {
            headers: {
                'session': `${localStorage.getItem('authToken')}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                alert(`${t('articles.confirmSubscribing')}`);
            })
            .catch(async error => {
                if (error.response && error.response.status === 400) {
                    const errorCode = await error.response.data;

                    switch (errorCode) {
                        case 1:
                            alert(t('articles.closed'));
                            break;
                        case 2:
                            alert(t('articles.ownerCannotSignUp'));
                            break;
                        case 3:
                            alert(t('articles.alreadySignedUp'));
                            break;
                        case 4:
                            alert(t('articles.articleNotFound'));
                            break;
                        default:
                            alert(t('articles.errorSubscribing'));
                    }
                } else if (error.response.status === 403) {
                    alert(t('articles.forbidden'));
                } else if (error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                } else {
                    alert(`${t('articles.errorSubscribing')} ${error}`);
                }
            });
    };

    const close = (id) => {
        axios.patch(`${import.meta.env.VITE_API_URL}/articles/${id}/close`, null, {
            headers: {
                session: `${localStorage.getItem('authToken')}` 
            }
        })
            .then(response => {
                const updatedArticles = articles.map(article => {
                    if (article.id === id) {
                        return {
                            ...article,
                            status: response.data.status
                        };
                    }
                    return article;
                });

                setArticles(updatedArticles);

                alert(t('articles.articleClosedSuccessfully'));
            })
            .catch(async error => {
                if (error.response && error.response.status === 400) {
                    const errorCode = await error.response.data;

                    switch (errorCode) {
                        case 1:
                            alert(t('articles.closed'));
                            break;
                        default:
                            alert(t('articles.errorClosingArticle'));
                    }
                } else if (error.response.status === 403) {
                    alert(t('articles.forbidden'));
                } else if (error.response.status === 401) {
                    alert(t('loggedout'));
                    handleLogout();
                } else {
                    alert(`${t('articles.errorClosingArticle')} ${error}`);
                }
            });
    };
    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('emailUser');
        onLogout();
        navigate('/'); // Redirect to home after logout
    };
    const handleLoginRedirect = () => {
        navigate('/login');
    };

    return (
        <Stack>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>{t('articles.image')}</th>
                        <th>{t('articles.name')}</th>
                        <th>{t('articles.link')}</th>
                        <th>{t('articles.price')}</th>
                        <th>{t('articles.recibes')}</th>
                        <th>{t('articles.status')}</th>
                        <th>{t('articles.deadline')}</th>
                        {!isUserFocus && isLoggedIn && (
                            <th>{t('articles.subscribe')}</th>
                        )}
                        {isUserFocus && (
                            <>
                                <th>{t('articles.subscriptors')}</th>
                                <th>{t('articles.close')}</th>
                            </>
                        )}
                    </tr>
                </thead>
                <tbody>
                    {articles.map(article => (
                        <tr key={article.id}>
                            <td><img src={article.image} alt={article.name} width="70" /></td>
                            <td>{article.name}</td>
                            <td>{article.link}</td>
                            <td>{article.cost}</td>
                            <td>{article.user_gets}</td>
                            <td>{article.status}</td>
                            <td>{article.deadline}</td>
                            {!isUserFocus && isLoggedIn && article.owner !== user && article.status === "OPEN" && (
                                <td>
                                    <Button onClick={() => subscribe(article.id)}>{t('articles.subscribe')}</Button>
                                </td>
                            )}
                            {isUserFocus && (
                                <>
                                    <td>
                                        <Button onClick={() => navigate(`/subscribers/${article.id}`)}>{t('articles.view')}</Button>
                                    </td>
                                </>
                            )}
                            {isUserFocus && article.status === "OPEN" && (
                                <>
                                    <td>
                                        <Button onClick={() => close(article.id)}>{t('articles.close')}</Button>
                                    </td>
                                </>
                            )}
                        </tr>
                    ))}
                </tbody>
            </Table>
            {isLoggedIn && (
                <Link to='/create'>{t('articles.createNew')}</Link>
            )}
        </Stack>
    );
}

export default ArticleList;