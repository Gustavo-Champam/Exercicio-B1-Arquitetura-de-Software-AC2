"""D5: broker MQTT real em loopback, publicador QoS1 e gateway com deduplicacao."""
import asyncio, json, threading
from amqtt.broker import Broker
import paho.mqtt.client as mqtt
async def main():
    broker=Broker({'listeners':{'default':{'type':'tcp','bind':'127.0.0.1:18883'}},'sys_interval':0,'auth':{'allow-anonymous':True}})
    await broker.start()
    try: await asyncio.to_thread(experimento)
    finally: await broker.shutdown()
def experimento():
    pronto=threading.Event();recebido=threading.Event();ids=set();eventos=[];mensagens=[]
    c=mqtt.Client(mqtt.CallbackAPIVersion.VERSION2,client_id='gateway-b1')
    def connect(client,*args): client.subscribe('instituicoes/facens/lab/presenca',qos=1)
    c.on_connect=connect
    c.on_subscribe=lambda *args: pronto.set()
    def message(client,userdata,msg):
        raw=json.loads(msg.payload);mensagens.append(raw)
        if raw['event_id'] not in ids:
            ids.add(raw['event_id']);eventos.append({'tipo':'PresencaRegistrada','instituicao':'facens','aluno':raw['aluno'],'event_id':raw['event_id']})
        if len(mensagens)==2: recebido.set()
    c.on_message=message;c.connect('127.0.0.1',18883);c.loop_start()
    try:
        assert pronto.wait(10),'assinatura sem ACK'
        for _ in range(2): c.publish('instituicoes/facens/lab/presenca',json.dumps({'event_id':'evt-1','aluno':'223645'}),qos=1).wait_for_publish(5)
        assert recebido.wait(10),'evento nao recebido'
        assert len(eventos)==1
        print(json.dumps({'mensagens_mqtt':len(mensagens),'efeitos':len(eventos),'evento_transformado':eventos[0]},ensure_ascii=False))
    finally: c.disconnect();c.loop_stop()
if __name__=='__main__': asyncio.run(main())
