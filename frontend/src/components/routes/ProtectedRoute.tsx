import React, { ReactElement } from 'react';
import { Route } from 'react-router-dom';
import Unauthorized from './Unauthorized';

type Props = {
    hasAccess: boolean,
    children: JSX.Element
}

const ProtectedRoute: React.FC<Props> = ({children, hasAccess}) => {

    if (hasAccess) {
        return children;
      } else{
        return <Unauthorized/>
      }
};

export default ProtectedRoute;