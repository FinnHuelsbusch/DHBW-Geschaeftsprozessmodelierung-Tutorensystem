import { message, Result } from 'antd';
import Text from 'antd/lib/typography/Text';
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyAccount } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { LoadingOutlined } from '@ant-design/icons';
import { AuthContext } from '../../context/UserContext';

const VerifyAccount: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(true);

    const handleVerifyError = () => {
        message.error("Ein Fehler ist aufgetreten");
        navigate(AppRoutes.Unauthorized);
    };

    useEffect(() => {
        const hash = searchParams.get("h");
        const email = searchParams.get("e");
        if (!hash || !email) {
            handleVerifyError();
        }
        verifyAccount(hash, email)
            .then(user => {
                setLoading(false);
                authContext.login(user);
                message.success("Registrierung erfolgreich", 2);
                navigate(AppRoutes.Main.Path);
            }).catch(err => handleVerifyError());
    }, []);

    return (
        <>
            {loading && <Result
                icon={<LoadingOutlined />}
                title="Prüfe...">
            </Result>}
            {!loading && <Result
                status="success"
                title="Ihr Konto wurde aktiviert"
                extra={
                    <Text>Sie können sich nun anmelden.</Text>
                }>
            </Result>}
        </>
    );
}

export default VerifyAccount;