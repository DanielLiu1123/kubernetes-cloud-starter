const http = require("http");

const host = '0.0.0.0';
const port = 8000;

const requestListener = (req, res) => {
    console.log(`Request received: ${req.url}`);
    console.log(`Request headers: ${JSON.stringify(req.headers)}`);
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('Hello, World! => ' + port);
};

const server = http.createServer(requestListener);
server.listen(port, host, () => {
    console.log(`Server is running on http://${host}:${port}`);
});