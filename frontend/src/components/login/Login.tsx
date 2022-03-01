import { Button, Divider, Form, Input, message, } from 'antd';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';


const Login: React.FC = () => {

    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    const [loading, setLoading] = useState(false);

    const onLogin = (values: any) => {
        setLoading(true);
        login(values.email, values.password)
            .then(user => {
                authContext.login(user);
                message.success("Login erfolgreich", 2);
                navigate(AppRoutes.Home);
            }).catch(err => {
                message.error("Login fehlgeschlagen");
                setLoading(false);
            });
    };


    const UserPasswordForm = () => (
        <Form
            name="login"
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 10 }}
            onFinish={onLogin}>
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
                <Button loading={loading} htmlType='submit' type='primary'>
                    Anmelden
                </Button>
                <Divider />
                Oder <a onClick={e => navigate(AppRoutes.Register, { replace: true })}>
                    registrieren.
                </a>
            </Form.Item>
        </Form>
    );

    return (
        <>
            <Title level={1}>
                Anmeldung
            </Title>
            <UserPasswordForm />
        </>
    )
}

export default Login;