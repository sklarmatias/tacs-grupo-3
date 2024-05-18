
import {
    createBrowserRouter,
    RouterProvider,
} from "react-router-dom";
import ArticleList from './articlesList.jsx';
import ArticleForm from './createArticle.jsx';
import LoginForm from './login.jsx';

const router = createBrowserRouter([
    {
        path: "/",
        element: <ArticleList />,
    },
    {
        path: "create",
        element: <ArticleForm />,
    },
    {
        path: "login",
        element: <LoginForm />,
    },
]);
function App() {
    return (
        <RouterProvider router={router} />
    )
}
export default App;