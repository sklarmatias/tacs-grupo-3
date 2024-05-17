import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { Stack, Table, Button } from 'react-bootstrap';

function ArticleList() {
    const [articles, setArticles] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:5283/articles')
            .then(response => {
                setArticles(response.data);
            })
            .catch(error => {
                alert('Error al obtener articulos:', error);
            });
    }, []);
    const subscribe = (id) => {
        axios.post('http://localhost:5283/articles/${id}/users/1') // Agregar el user cuando se haga el login
            .then(response => {
                alert('Ok');
            })
            .catch(error => {
                alert('Error al obtener articulos:', error);
            });
    };
    const close = (id) => {
        axios.post('http://localhost:5283/articles/${id}/close')
            .then(response => {
                alert('Ok');
            })
            .catch(error => {
                alert('Error al obtener articulos:', error);
            });
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
                    <th>Suscribir</th>
                    <th>Cerrar</th>
                    <th>Ver suscriptos</th>
                </tr>
            </thead>
            <tbody>
                {articles.map(article => (
                    <tr>
                        <th><Image src={article.image} rounded /></th>
                        <th>{article.name}</th>
                        <th>{article.link}</th>
                        <th>{article.cost}</th>
                        <th>{article.userGets}</th>
                        <th>
                            <Button onClick={() => subscribe(article.id)}>Subscribir</Button>
                        </th>
                        <th>
                            <Button onClick={() => close(article.id)}>Cerrar</Button>
                        </th>
                        <th>
                            <Link to={'/subscriptors?id=' + article.id }>Crear</Link>
                        </th>
                    </tr>
                ))}
            </tbody>
            </Table>
        <Link to='/create'>Crear</Link>
        </Stack>
    );
}

export default ArticleList;