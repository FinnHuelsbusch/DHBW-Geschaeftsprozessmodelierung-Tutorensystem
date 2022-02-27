import { Button, Form, Input, message, } from 'antd';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';

const Login: React.FC = () => {

    const navigate = useNavigate();

    const onSubmit = (values: any) => {
        login(values.email, values.password)
            .then(user => {
                // TODO: authContext.login
                message.success("Login erfolgreich");
                navigate(AppRoutes.Home);
            }, err => message.error("Login fehlgeschlagen"));
    }

    // const [loginForm] = useForm();

    return (
        <>
            <Form
                // form={loginForm}
                name="login"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 10 }}
                onFinish={onSubmit}>
                <Form.Item
                    label="E-Mail"
                    name="email"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input type={'email'} />
                </Form.Item>
                <Form.Item
                    label="Passwort"
                    name="password"
                    rules={[{ required: true, message: 'Pflichtfeld' }]}>
                    <Input.Password />
                </Form.Item>
                <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                    <Button htmlType='submit' type='primary'>
                        Anmelden
                    </Button>
                </Form.Item>
            </Form>
        </>
    )
}

export default Login;