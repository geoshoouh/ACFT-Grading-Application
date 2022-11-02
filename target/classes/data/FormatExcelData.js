$("#run").click(() => tryCatch(run));


function run() {
  return Excel.run(async function(context) {
    let sheet = context.workbook.worksheets.getActiveWorksheet().getRange("C5:U35");
    sheet.unmerge();
    sheet.load("values");
    let bottomBar = context.workbook.worksheets.getActiveWorksheet().getRange("B44:U44");
    bottomBar.unmerge();
    bottomBar.load("values");
    await context.sync();
    let m = sheet.values.length;
    let n = sheet.values[0].length;
    for (let i = 0; i < m; i+=10) {
      for (let j = 0; j < n; j+=2) {
        let valueArray = sheet.values[i][j].split(/(?:\n| )+/);
        console.log(`for cell (${i}, ${j}) with contents ${valueArray}`);
        for (let k = 0; k < valueArray.length; k++){
          console.log(`Placing ${valueArray[k]} into cell (${i+k}, ${j})` );
          sheet.getCell(i+k, j).values = [[valueArray[k]]];
        }
      }
    }
    for (let i = 0; i < bottomBar.values[0].length; i+=2){
      let valueArray = bottomBar.values[0][i].split(/\s+/);
      bottomBar.getCell(0, i).values = [[valueArray[0]]];
      bottomBar.getCell(0, i+1).values = [[valueArray[1]]];
    }
    return context.sync();
  });
}


/** Default helper for invoking an action and handling errors. */
function tryCatch(callback) {
  Promise.resolve()
    .then(callback)
    .catch(function(error) {
      // Note: In a production add-in, you'd want to notify the user through your add-in's UI.
      console.error(error);
    });
}
