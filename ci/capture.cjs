const {chromium}=require('../.ci-runtime/node_modules/playwright');
(async()=>{const b=await chromium.launch({headless:true});const page=await b.newPage({viewport:{width:1440,height:1200}});
page.on('pageerror',e=>console.error('GRAFANA PAGE ERROR:',e.message));
for(const [port,uid,name] of [[3004,'b1-core','b1-grafana'],[3003,'a3-jogos','a3-grafana']]){
 await page.goto(`http://127.0.0.1:${port}/login`);await page.getByPlaceholder('Email or username').fill('admin');await page.getByPlaceholder('Password').fill('admin');await page.getByRole('button',{name:'Log in',exact:true}).click();
 const skip=page.getByRole('button',{name:'Skip',exact:true});await skip.waitFor({timeout:15000}).catch(()=>{});if(await skip.isVisible())await skip.click();
 await page.goto(`http://127.0.0.1:${port}/d/${uid}?from=now-15m&to=now`);
 await page.getByText('Coleta da API',{exact:true}).waitFor({timeout:60000});
 await page.getByText(uid==='b1-core'?'Fallback de IA':'Jogos finalizados',{exact:true}).scrollIntoViewIfNeeded();
 await page.waitForTimeout(8000);await page.screenshot({path:`evidence/${name}.png`,fullPage:true});
}
await b.close();})().catch(e=>{console.error(e);process.exit(1)});
