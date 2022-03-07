import { Button, Divider, Form, Input, Layout, message, Result } from 'antd';
import React, { useContext, useEffect, useState } from 'react';
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { enableAccount, getRequestError, performPasswordReset } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { LoadingOutlined } from '@ant-design/icons';
import { AuthContext } from '../../context/UserContext';
import { ErrorCode, getErrorMessageString } from '../../types/RequestError';
import EmailFormInput from '../inputs/EmailFormInput';
import Paragraph from 'antd/lib/typography/Paragraph';
import { Content } from 'antd/lib/layout/layout';

const VerifyResetPassword: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(false);

    const handleVerifyError = (messageStr: string) => {
        message.error(messageStr);
        navigate(AppRoutes.Unauthorized);
    };

    const onSubmit = (values: any) => {
        const hash = searchParams.get("h");
        const email = searchParams.get("e");
        if (!hash || !email) {
            handleVerifyError("Ein Fehler ist aufgetreten.");
        }
        setLoading(true);
        performPasswordReset(hash, email, values.newPassword)
            .then(user => {
                setLoading(false);
                authContext.login(user);
                message.success("Passwort erfolgreich geändert", 2);
                navigate(AppRoutes.Main.Path);
            }).catch(err => {
                message.error("Verifizierung fehlgeschlagen");
                setLoading(false);
            });
    }

    return (
        <>
            <Layout>
                <Content style={{
                    marginLeft: '20%',
                    marginRight: '20%',
                    marginTop: '10vh',
                    marginBottom: '70vh'
                }}>
                    <Form
                        name="login"
                        labelCol={{ span: 8 }}
                        wrapperCol={{ span: 14 }}
                        initialValues={{ email: searchParams.get("e") }}
                        onFinish={onSubmit}>
                        <Paragraph>
                            Geben Sie bitte das Passwort ein, das Sie zuvor neu gesetzt haben.
                        </Paragraph>
                        <Divider />
                        <EmailFormInput disabled />
                        <Form.Item
                            label="Passwort"
                            name="newPassword"
                            rules={[{ required: true}]}>
                            <Input.Password disabled={loading} />
                        </Form.Item>
                        <Form.Item wrapperCol={{ offset: 8, span: 10 }}>
                            <Button htmlType='submit' type='primary' loading={loading}>
                                Absenden
                            </Button>
                        </Form.Item>
                    </Form>
                </Content>
            </Layout>
        </>
    );
}

export default VerifyResetPassword;