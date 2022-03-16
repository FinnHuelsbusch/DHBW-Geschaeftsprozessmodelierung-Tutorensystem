import { Button, Dropdown, Menu, message } from 'antd';
import React, { useContext } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { User, UserRole } from '../../types/User';
import './Navigation.scss';
import { UserOutlined } from '@ant-design/icons'
import Sider from 'antd/lib/layout/Sider';

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

    // note: keys must be unique!
    const MenuKeys = {
        Overview: '1',
        Tutorials: '2',
        AdminOverview: '3',
        DirectorOverview: '4',
        Login: '5'
    };

    return (
        <Menu
            mode='vertical'
            theme='light'
            defaultSelectedKeys={['1']}>

            <Menu.Item key={MenuKeys.Overview}>
                <Link to={AppRoutes.Main.Path}>
                    <div className='app-home'>
                        <i>Tutorensystem</i>
                    </div>
                </Link>
            </Menu.Item>

            <Menu.Item key={MenuKeys.Tutorials}>
                <Link to={AppRoutes.Main.Subroutes.Tutorials}>
                    Tutorien
                </Link>
            </Menu.Item>

            {authContext.hasRoles([UserRole.ROLE_ADMIN])
                && <Menu.Item key={MenuKeys.AdminOverview}>
                    <Link to={AppRoutes.Main.Subroutes.AdminOverview}>
                        Übersicht Administrator
                    </Link>
                </Menu.Item>}

            {authContext.hasRoles([UserRole.ROLE_DIRECTOR])
                && <Menu.Item key={MenuKeys.DirectorOverview}>

                    Übersicht Studiengangsleiter
                </Menu.Item>}

            <Menu.Item key={MenuKeys.Login} icon={<UserOutlined />}>
                {authContext.loggedUser && <ProfileButton {...authContext.loggedUser} />}
                {!authContext.loggedUser && <LoginButton />}
            </Menu.Item>

        </Menu >
    );
}

export default Navigation;