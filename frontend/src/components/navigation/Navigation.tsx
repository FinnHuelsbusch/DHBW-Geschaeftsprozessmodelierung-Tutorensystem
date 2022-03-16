import { Dropdown, Menu, message } from 'antd';
import React, { useContext } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { User, UserRole } from '../../types/User';
import './Navigation.scss';
import { UserOutlined } from '@ant-design/icons'
import SubMenu from 'antd/lib/menu/SubMenu';

const Navigation: React.FC = () => {

    const authContext = useContext(AuthContext);
    const navigate = useNavigate();
    const { pathname } = useLocation();


    const ProfileButton = (user: User) => {
        const onLogoutClick = () => {
            navigate(AppRoutes.Main.Path);
            authContext.logout();
            message.info("Sie wurden ausgeloggt", 2);
        }

        return (
            <SubMenu
                key={MenuKeys.Profile.Main}
                icon={<UserOutlined />}
                title={user.email}>
                <Menu.Item key={MenuKeys.Profile.Settings}>
                    <Link to={AppRoutes.Main.Subroutes.Settings}>
                        Einstellungen
                    </Link>
                </Menu.Item>
                <Menu.Item key={MenuKeys.Profile.Logout} onClick={onLogoutClick}>
                    Logout
                </Menu.Item>
            </SubMenu>
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
        Login: '5',
        Profile: {
            Main: '6',
            Settings: '6 1',
            Logout: '6 2',
        }
    };

    const getSelectedMenuKey = (): string => {
        console.log(pathname);

        switch (pathname) {
            case AppRoutes.Main.Path:
                return MenuKeys.Overview;

            case AppRoutes.Main.Subroutes.Tutorials
                || AppRoutes.Main.Subroutes.TutorialDetails:
                return MenuKeys.Tutorials;

            case AppRoutes.Main.Subroutes.AdminOverview:
                return MenuKeys.AdminOverview;

            case AppRoutes.Main.Subroutes.DirectorOverview:
                return MenuKeys.DirectorOverview;

            case AppRoutes.Main.Subroutes.Login:
                return MenuKeys.Login;

            case AppRoutes.Main.Subroutes.Settings:
                return MenuKeys.Profile.Main;

            default:
                return MenuKeys.Overview;
        }
    };

    console.log(getSelectedMenuKey());


    return (
        <Menu
            mode='horizontal'
            theme='dark'
            defaultSelectedKeys={[getSelectedMenuKey()]}
        >

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
                    <Link to={AppRoutes.Main.Subroutes.DirectorOverview}>
                        Übersicht Studiengangsleiter
                    </Link>
                </Menu.Item>}

            {!authContext.loggedUser
                && <Menu.Item key={MenuKeys.Login} icon={<UserOutlined />}>
                    {!authContext.loggedUser && <LoginButton />}
                </Menu.Item>
            }

            {authContext.loggedUser
                && <ProfileButton {...authContext.loggedUser} />
            }

        </Menu >
    );
}

export default Navigation;