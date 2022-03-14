export const AppRoutes = {
    Main: {
        Path: '/',
        Subroutes: {
            Login: "/login",
            Register: "/register",
            Settings: "/settings",
            AdminOverview: "/adminOverview",
            DirectorOverview: "/directorOverview",
            Tutorials: "/tutorials",
            TutorialDetails: "/tutorials/:tutorialId"
        }
    },
    VerifyRegistration: '/verifyRegistration',
    VerifyResetPassword: 'verifyResetPassword',
    Unauthorized: '/unauthorized'
}