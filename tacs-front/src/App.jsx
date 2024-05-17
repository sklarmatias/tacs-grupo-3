
import {
    createBrowserRouter,
    RouterProvider,
} from "react-router-dom";
import ArticleList from './articlesList.jsx';
import ArticleForm from './createArticle.jsx';

const router = createBrowserRouter([
    {
        path: "/",
        element: <ArticleList />,
    },
    {
        path: "create",
        element: <ArticleForm />,
    },
]);
function App() {
    return (
        <RouterProvider router={router} />
    )
}
export default App;