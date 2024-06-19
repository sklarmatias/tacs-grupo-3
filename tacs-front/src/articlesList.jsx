import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import { Stack, Table, Button } from 'react-bootstrap';

function ArticleList({ userFocus }) {
    const [articles, setArticles] = useState([]);
    const [isLoggedIn, setIsLoggedIn] = useState(false); // State to track login status
    const [isUserFocus, setIsUserFocus] = useState(false);
    const [user, setUser] = useState('');
    const navigate = useNavigate();

    useEffect(() => {

            const token = localStorage.getItem('authToken');
        if (userFocus === 'true') {
            axios.get(`${import.meta.env.VITE_REACT_APP_API_URL}/articles`, {
                headers: {
                    'user': token
                }
            })
                .then(response => {
                    setArticles(response.data);
                })
                .catch(error => {
                    alert('Error al obtener artículos:', error);
                });
            setIsUserFocus(true);
        } else {
            axios.get(`${import.meta.env.VITE_REACT_APP_API_URL}/articles`)
                .then(response => {
                    setArticles(response.data);
                })
                .catch(error => {
                    alert('Error al obtener artículos:', error);
                });
        }

        // Check if user is logged in
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
    }, [userFocus]);


    const subscribe = (id) => {
        axios.post(`${import.meta.env.VITE_REACT_APP_API_URL}/users/`, '', {
            headers: {
                'user': `${localStorage.getItem('authToken')}`,
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                alert('Ok');
            })
            .catch(error => {
                alert('Error al suscribirse:', error);
            });
    };

    const close = (id) => {
        axios.patch(`${import.meta.env.VITE_REACT_APP_API_URL}/close`, null, {
            headers: {
                user: `${localStorage.getItem('authToken')}` 
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

                alert('Artículo cerrado exitosamente');
            })
            .catch(error => {
                alert('Error al cerrar artículo:', error);
            });
    };

    const handleLoginRedirect = () => {
        navigate('/login');
    };

    return (
        <Stack>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>Imagen</th>
                        <th>Nombre</th>
                        <th>Enlace</th>
                        <th>Costo</th>
                        <th>Recibe</th>
                        <th>Estado</th>
                        <th>Fecha Limite</th>
                        {!isUserFocus && isLoggedIn && (
                            <th>Suscribir</th>
                        )}
                        {isUserFocus && (
                            <>
                                <th>Suscriptores</th>
                                <th>Cerrar</th>
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
                            {!isUserFocus && isLoggedIn && article.owner !== user && article.status == "OPEN" && (
                                <td>
                                    <Button onClick={() => subscribe(article.id)}>Subscribir</Button>
                                </td>
                            )}
                            {isUserFocus && (
                                <>
                                    <td>
                                        <Button onClick={() => navigate(`/subscribers/${article.id}`)}>Ver</Button>
                                    </td>
                                </>
                            )}
                            {isUserFocus && article.status == "OPEN" && (
                                <>
                                    <td>
                                        <Button onClick={() => close(article.id)}>Cerrar</Button>
                                    </td>
                                </>
                            )}
                        </tr>
                    ))}
                </tbody>
            </Table>
            {isLoggedIn && (
                <Link to='/create'>Crear</Link>
            )}
        </Stack>
    );
}

export default ArticleList;