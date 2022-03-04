import { message, Result } from 'antd';
import Text from 'antd/lib/typography/Text';
import React, { useContext, useEffect, useState } from 'react';
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { enableAccount, getRequestError } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { LoadingOutlined } from '@ant-design/icons';
import { AuthContext } from '../../context/UserContext';
import { getErrorMessageString } from '../../types/RequestError';

const VerifyRegistration: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(true);

    const handleVerifyError = (messageStr: string) => {
        message.error(messageStr);
        navigate(AppRoutes.Unauthorized);
    };

    useEffect(() => {
        const hash = searchParams.get("h");
        const email = searchParams.get("e");
        if (!hash || !email) {
            handleVerifyError("Ein Fehler ist aufgetreten.");
        }
        enableAccount(hash, email)
            .then(user => {
                setLoading(false);
                authContext.login(user);
                message.success("Registrierung erfolgreich", 2);
                navigate(AppRoutes.Main.Path);
            }).catch(err =>
                handleVerifyError(getErrorMessageString(getRequestError(err).errorCode))
            );
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

export default VerifyRegistration;