import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const LoginForm = ({ onLogin, isRegister }) => {
    const [formData, setFormData] = useState({
        email: '',
        pass: '',
        confirmpass: isRegister ? '' : undefined,
        name: isRegister ? '' : undefined,
        surname: isRegister ? '' : undefined
    });
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (isRegister) {
            registerUser(formData);
        } else {
            loginUser(formData);
        }
    };

    const loginUser = (formData) => {
        fetch('http://localhost:8080/restapp/users/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: formData.email, pass: formData.pass }),
        })
            .then((response) => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error('Login failed');
            })
            .then((data) => {
                localStorage.setItem('authToken', data.token);
                localStorage.setItem('emailUser', formData.email);
                onLogin(formData.email);
                navigate('/');
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Error logging in');
            });
    };

    const registerUser = (formData) => {
        fetch('http://localhost:8080/restapp/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: formData.email,
                pass: formData.pass,
                name: formData.name,
                surname: formData.surname
            }),
        })
            .then((response) => {
                if (response.status === 201) {
                    alert('Registration successful');
                    navigate('/login');
                } else {
                    alert('Error registering');
                }
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('Error registering');
            });
    };

    return (
        <Form onSubmit={handleSubmit}>
            {isRegister && (
                <>
                    <Form.Group controlId="name">
                        <Form.Label>Name</Form.Label>
                        <Form.Control
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Enter your name"
                            required
                        />
                    </Form.Group>
                    <Form.Group controlId="surname">
                        <Form.Label>Surname</Form.Label>
                        <Form.Control
                            type="text"
                            name="surname"
                            value={formData.surname}
                            onChange={handleChange}
                            placeholder="Enter your surname"
                            required
                        />
                    </Form.Group>
                </>
            )}
            <Form.Group controlId="email">
                <Form.Label>Email</Form.Label>
                <Form.Control
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Enter email"
                    required
                />
            </Form.Group>
            <Form.Group controlId="pass">
                <Form.Label>pass</Form.Label>
                <Form.Control
                    type="pass"
                    name="pass"
                    value={formData.pass}
                    onChange={handleChange}
                    placeholder="Enter pass"
                    required
                />
            </Form.Group>
            {isRegister && (
                <Form.Group controlId="confirmpass">
                    <Form.Label>Confirm pass</Form.Label>
                    <Form.Control
                        type="pass"
                        name="confirmpass"
                        value={formData.confirmpass}
                        onChange={handleChange}
                        placeholder="Confirm pass"
                        required
                    />
                </Form.Group>
            )}
            <Button variant="primary" type="submit">
                {isRegister ? 'Register' : 'Login'}
            </Button>
        </Form>
    );
};

export default LoginForm;