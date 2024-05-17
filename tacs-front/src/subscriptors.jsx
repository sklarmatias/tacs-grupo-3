function Subscriptors() {
    const [searchParams, setSearchParams] = useSearchParams();
    const id = searchParams.get("id");
    const [annotations, setAnnotations] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:5283/articles/${id}/users')
            .then(response => {
                setAnnotations(response.data);
            })
            .catch(error => {
                alert('Error al obtener usuarios:', error);
            });
    }, []);
    

    return (
        <Stack>
            <Table striped bordered hover>
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Email</th>
                        <th>fecha</th>
                    </tr>
                </thead>
                <tbody>
                    {annotations.map(annotation => (
                        <tr>
                            <th>{annotation.user.name}</th>
                            <th>{annotation.user.surname}</th>
                            <th>{annotation.user.email}</th>
                            <th>{annotation.date}</th>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </Stack>
    );
}

export default Subscriptors;