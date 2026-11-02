"""D4: compara falha sincrona com outbox transacional duravel em SQLite."""
import sqlite3, tempfile, unittest, threading, urllib.request, urllib.error
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
def abrir(path):
    db=sqlite3.connect(path)
    db.executescript('CREATE TABLE IF NOT EXISTS conclusoes(id TEXT PRIMARY KEY); CREATE TABLE IF NOT EXISTS outbox(id TEXT PRIMARY KEY, enviado INTEGER DEFAULT 0); CREATE TABLE IF NOT EXISTS recebidos(id TEXT PRIMARY KEY);')
    return db
def concluir(db, id):
    with db:
        db.execute('INSERT OR IGNORE INTO conclusoes VALUES (?)',(id,))
        db.execute('INSERT OR IGNORE INTO outbox(id) VALUES (?)',(id,))
def entregar(db, consumidor):
    for (id,) in db.execute('SELECT id FROM outbox WHERE enviado=0').fetchall():
        consumidor(id)  # falha deixa mensagem pendente para proxima tentativa
        with db: db.execute('UPDATE outbox SET enviado=1 WHERE id=?',(id,))
class EventosTest(unittest.TestCase):
    def test_falha_recuperacao_e_duplicidade(self):
        estado={'online':False,'recebidos':set()}
        class Notificacao(BaseHTTPRequestHandler):
            def do_POST(self):
                id=self.rfile.read(int(self.headers['Content-Length'])).decode()
                if not estado['online']:
                    self.send_response(503);self.end_headers();return
                estado['recebidos'].add(id)
                self.send_response(204);self.end_headers()
            def log_message(self,*args): pass
        servidor=ThreadingHTTPServer(('127.0.0.1',0),Notificacao)
        thread=threading.Thread(target=servidor.serve_forever,daemon=True);thread.start()
        def http(id):
            with urllib.request.urlopen(urllib.request.Request(
                f'http://127.0.0.1:{servidor.server_port}/notificar',data=id.encode()),timeout=1) as response:
                self.assertEqual(204,response.status)
        with tempfile.TemporaryDirectory() as tmp:
            path=Path(tmp)/'lab.db';db=abrir(path)
            with self.assertRaises(urllib.error.HTTPError): http('sincrono')
            concluir(db,'curso-1')
            with self.assertRaises(urllib.error.HTTPError): entregar(db,http)
            self.assertEqual(1,db.execute('SELECT count(*) FROM conclusoes').fetchone()[0])
            db.close();db=abrir(path) # pendencia sobrevive ao reinicio
            estado['online']=True
            entregar(db,http);concluir(db,'curso-1');entregar(db,http)
            http('curso-1') # entrega repetida depois da recuperacao
            self.assertEqual({'curso-1'},estado['recebidos'])
            self.assertEqual(0,db.execute('SELECT count(*) FROM outbox WHERE enviado=0').fetchone()[0])
            db.close()
            print('D4: REST falha; conclusao+outbox persistem; reinicio recupera; duplicata nao duplica efeito.')
        servidor.shutdown();servidor.server_close();thread.join()
if __name__=='__main__': unittest.main()
