import { Button, Form, Input, Modal } from 'antd';
import { useForm } from 'antd/lib/form/Form';
import { format } from 'path/posix';
import React, { useState } from 'react';
import { login } from '../../api/api';
import { User } from '../../types/User';

type Props = {
    isVisible: boolean,
    onCancel: () => void,
    onSuccess: () => void
}

const Login: React.FC<Props> = ({ isVisible, onCancel, onSuccess }) => {

    // const [isModalVisible, setIsModalVisible] = useState(isVisible);

    const onSubmit = (values: any) => {
        login(values.email, values.password)
            .then(user => {
                // TODO: authContext.login
                onSuccess();
            });
    }

    const [loginForm] = useForm();

    return (
        <>
            <Modal
                title="Login"
                afterClose={() => loginForm.resetFields()}
                visible={isVisible}
                onCancel={e => onCancel()}
                footer={[
                    <Button key="back" onClick={onCancel}>
                        Abbrechen
                    </Button>,
                    <Button key="submit" type='primary' onClick={e => loginForm.submit()}>
                        Anmelden
                    </Button>
                ]}>
                <Form
                    form={loginForm}
                    name="login"
                    labelCol={{ span: 8 }}
                    wrapperCol={{ span: 16 }}
                    onFinish={onSubmit}>
                    <Form.Item
                        label="E-Mail"
                        name="email"
                        rules={[{ required: true, message: 'Pflichtfeld' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item
                        label="Passwort"
                        name="password"
                        rules={[{ required: true, message: 'Pflichtfeld' }]}>
                        <Input.Password />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
}

export default Login;