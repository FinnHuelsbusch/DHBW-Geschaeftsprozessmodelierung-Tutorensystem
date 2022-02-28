import { Button, Result } from 'antd';
import React from 'react';
import { useNavigate } from 'react-router-dom';

const Unauthorized: React.FC = () => {

    const navigate = useNavigate();

    return (
        <Result
            status="error"
            title="Nicht autorisiert"
            subTitle="Auf diese Seite darfst du nicht zugreifen!"
            extra={
                <Button onClick={e => navigate("/")}>
                    Zurück zur Startseite
                </Button>
            }>

        </Result>
    );
};

export default Unauthorized;