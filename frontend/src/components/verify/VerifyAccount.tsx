import { message, Result } from 'antd';
import Text from 'antd/lib/typography/Text';
import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { verifyAccount } from '../../api/api';
import { AppRoutes } from '../../types/AppRoutes';
import { LoadingOutlined } from '@ant-design/icons';

const VerifyAccount: React.FC = () => {

    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const hash = searchParams.get("h");
        const email = searchParams.get("e");
        verifyAccount(hash, email)
            .then(res => {
                setLoading(false);
            }).catch(err => {
                message.error("Ein Fehler ist aufgetreten");
                navigate(AppRoutes.Unauthorized);
            });
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