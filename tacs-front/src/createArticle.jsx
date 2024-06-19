import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const ArticleForm = () => {
    const [formData, setFormData] = useState({
        name: '',
        image: '',
        deadline: '',
        users_max: '',
        users_min: '',
        cost: '',
        cost_type: 'TOTAL',
        user_gets: ''
    });
    const navigate = useNavigate();


    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        postFormData(formData);

    };

    const postFormData = (formData) => {
        fetch('${import.meta.env.VITE_REACT_APP_API_URL}/articles', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'user': `${localStorage.getItem('authToken')}`
            },
            body: JSON.stringify(formData)
        })
            .then((response) => {
                if (response.status === 201) {
                    alert('Article created successfully');
                    navigate('/myarticles');
                } else {
                    alert('Error creating article');
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Error creating article');
            });
    };

    return (
        <Form onSubmit={handleSubmit}>
            <Form.Group controlId="name">
                <Form.Label>Article Name</Form.Label>
                <Form.Control
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Enter article name"
                />
            </Form.Group>
            <Form.Group controlId="image">
                <Form.Label>Image URL</Form.Label>
                <Form.Control
                    type="text"
                    name="image"
                    value={formData.image}
                    onChange={handleChange}
                    placeholder="Enter image URL"
                />
            </Form.Group>
            <Form.Group controlId="deadline">
                <Form.Label>Deadline</Form.Label>
                <Form.Control
                    type="date"
                    name="deadline"
                    value={formData.deadline}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="users_max">
                <Form.Label>Maximum Users</Form.Label>
                <Form.Control
                    type="number"
                    name="users_max"
                    value={formData.users_max}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="users_min">
                <Form.Label>Minimum Users</Form.Label>
                <Form.Control
                    type="number"
                    name="users_min"
                    value={formData.users_min}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="cost">
                <Form.Label>Cost</Form.Label>
                <Form.Control
                    type="number"
                    name="cost"
                    value={formData.cost}
                    onChange={handleChange}
                />
            </Form.Group>
            <Form.Group controlId="cost_type">
                <Form.Label>Cost Type</Form.Label>
                <Form.Control
                    as="select"
                    name="cost_type"
                    value={formData.cost_type}
                    onChange={handleChange}
                    
                >
                    <option value="TOTAL">TOTAL</option>
                    <option value="PER_USER">PER_USER</option>
                </Form.Control>
            </Form.Group>
            <Form.Group controlId="user_gets">
                <Form.Label>User Gets</Form.Label>
                <Form.Control
                    type="text"
                    name="user_gets"
                    value={formData.user_gets}
                    onChange={handleChange}
                />
            </Form.Group>
            <Button variant="primary" type="submit">
                Submit
            </Button>
        </Form>
    );
};


export default ArticleForm;