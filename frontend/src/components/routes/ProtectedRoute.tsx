import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppRoutes } from '../../types/AppRoutes';

type Props = {
    hasAccess: boolean,
    children: JSX.Element
}

const ProtectedRoute: React.FC<Props> = ({ children, hasAccess }) => {

    const navigate = useNavigate();

    useEffect(() => {
        if (!hasAccess) {
            navigate(AppRoutes.Unauthorized, { replace: true });
        }
    }, []);

    if (hasAccess) {
        return children;
    } else {
        // must wait for component to render, then navigate to unauthorized route
        return <></>;
    }
};

export default ProtectedRoute;