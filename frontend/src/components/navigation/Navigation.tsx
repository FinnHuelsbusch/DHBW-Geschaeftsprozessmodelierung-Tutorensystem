import { Button, Dropdown, Menu, message } from 'antd';
import React, { useContext } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { User, UserRole } from '../../types/User';
import './Navigation.scss';
import { UserOutlined } from '@ant-design/icons'

const Navigation: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();

    const ProfileButton = (user: User) => {
        const onLogoutClick = () => {
            navigate(AppRoutes.Main.Path);
            authContext.logout();
            message.info("Sie wurden ausgeloggt", 2);
        }
        const profileDropdown = (
            <Menu>
                <Menu.Item key="settings">
                    <Link to={AppRoutes.Main.Subroutes.Settings}>
                        Einstellungen
                    </Link>
                </Menu.Item>
                <Menu.Item key="logout" onClick={onLogoutClick}>
                    Logout
                </Menu.Item>
            </Menu>
        );

        return (
            <Dropdown overlay={profileDropdown}
                trigger={['click']}>
                {/* <a className='login-button' onClick={e => e.preventDefault()}> */}
                <Button type='default'>
                    <UserOutlined /> {user.email}
                </Button>
            </Dropdown>
        );
    }

    const LoginButton = () => {
        return (
            <Link to={AppRoutes.Main.Subroutes.Login}>
                Anmelden
            </Link>
        );
    }

    return (
        <Menu mode='horizontal' theme='dark'
            defaultSelectedKeys={['1']}>
            <Menu.Item key="1">
                <Link to={AppRoutes.Main.Path}>
                    <div className='app-home'>
                        <i>Tutorensystem</i>
                    </div>
                </Link>
            </Menu.Item>
            <Menu.Item key="2">
                <Link to={AppRoutes.Main.Subroutes.Tutorials}>
                    Tutorien
                </Link>
            </Menu.Item>
            {authContext.hasRoles([UserRole.ROLE_ADMIN])
                && <Menu.Item key="11">
                    <Link to={AppRoutes.Main.Subroutes.AdminOverview}>
                        Übersicht Administrator
                    </Link>
                </Menu.Item>}
            {authContext.hasRoles([UserRole.ROLE_DIRECTOR])
                && <Menu.Item key="12">
                    Übersicht Studiengangsleiter
                </Menu.Item>}
            <li className='navbar-profile'>
                {authContext.loggedUser && <ProfileButton {...authContext.loggedUser} />}
                {!authContext.loggedUser && <LoginButton />}
            </li>

        </Menu >
    );
}

export default Navigation;