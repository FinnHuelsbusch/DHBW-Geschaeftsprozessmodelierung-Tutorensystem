import { Menu } from 'antd';
import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { UserRole } from '../../types/User';
import './Navigation.scss';

const Navigation: React.FC = () => {

    const authContext = useContext(AuthContext);

    return (
        <Menu mode='horizontal' theme='dark'
            defaultSelectedKeys={['1']}>
            <Menu.Item key="1">
                <Link to={AppRoutes.Home}>
                    <div className='app-home'>
                        <i>Tutorensystem</i>
                    </div>
                </Link>
            </Menu.Item>
            {authContext.hasRoles([UserRole.ROLE_ADMIN])
                && <Menu.Item key="2">
                    <Link to={AppRoutes.AdminOverview}>AdminOverview</Link>
                </Menu.Item>}
            {authContext.hasRoles([UserRole.ROLE_ADMIN])
                && <Menu.Item key="3">
                    DirectorOverview
                </Menu.Item>}
        </Menu>
    )
}

export default Navigation;