function initMills(){
	makeCoins();

	state = "";
	turn =1;
	remaining = [9,9];
	unused = [9,9];
	move = [-1,-1,-1];
	thisspot='';
	clickable = 'true';
	$('.spot').attr({value: 'empty', valid: 'true'});
	
    $("#s1").text("YOUR TURN");
}

on = 9;

function makeCoins()
{
	var coins = $("#coins");
	for(var i=0; i<9;i++)
	{
		var div = $("<div class = 'xcoin' clicked='false' onboard ='null'>  </div>" );
		
		coins.append(div);
		
        div.click(function(){
          if (clickable == 'true' &&(on<=0||(on>0 && $(this).attr('onboard')=='null'))){
             $('.xcoin').each(function(index){
				
				$(this).css('borderColor', 'white' );
				$(this).attr('clicked', 'false');
				
			});
        	if ($(this).attr('clicked')=='false'){
        	    $(this).attr('clicked', 'true');
			    $(this).css('borderColor','yellow');
			    if ($(this).attr('onboard')=='null'){
			    	move[0]=-1;
			    	console.log("movefrom "+ move[0]);
			    }


			}
			else{
				$(this).attr('clicked', 'false');
			    $(this).css('borderColor','white');
			}
			on--;
			
         }
			
        });
	}

	var div1 = $("<div class='middle'> <p id = 's1'></div>");
	coins.append(div1);
	for(var i=0; i<9;i++)
	{
		var div2 = $("<div class = 'ycoin' remove = 'false'> </div>" );
		coins.append(div2);

		div2.click(function(){
			if ($(this).attr('remove')=='true'){
				$(this).parent('.spot').attr('value', 'empty');
				$(this).remove();
				remaining[0]--;
                
			    
					$('.ycoin').each(function(){
						$(this).attr('remove', 'false');
						$(this).css('borderColor', 'white');
					});
				if (remaining[0]>=3){
					doTurn();
			   }
			   else{
			   	     clickable = 'false';
			   	     $('#s1').text("You WIN!");
			   }

		    }
			
		});

	}




     $('.spot').attr({value: 'empty', valid: 'true'});
     $('.spot').attr('clicked','false');
	$('.spot').click(function(){
		
		if (clickable=='true'){
			if ($(this).attr('value')=='empty'){
				var s = $(this).attr('id');
	        	s = s.substring(4,s.length);
	        	move[1]= parseInt(s);
	        	console.log("moveto "+ move[1]);
				thisspot = $(this);
				state = encodeState();
		        $.getJSON( "http://localhost:8080/check.html?" + state, checkMove );

	        }
	        else if ($(this).attr('value')== 'x'){
	        	var t = $(this).attr('id');
	        	t = t.substring(4,t.length);
	        	move[0]= parseInt(t);
	        	console.log("movefrom "+ move[0]);
			    $(this).find('.xcoin');

			}
	   }

	});	
}


function xcoinclick(div){
	
        if ($(div).attr('clicked')=='false'){
        	    $(div).attr('clicked', 'true');
			    $(div).css('borderColor','yellow');
			}
			else{
				$(div).attr('clicked', 'false');
			    $(div).css('borderColor','red');
			}  
}

function toChar(elt)
{
    if ($( elt ).attr('value') == 'empty')
	{
	    return '.';
	}
    else if ($( elt).attr('value') == 'x')
	{
	    return 'x';
	}
    else
	{
	    return 'y';
	}
}

function encodeState()
{
    var spots = "";
    $( ".spot" ).each( function () { spots = spots + toChar(this); } );
    state = spots + ";" + turn +";"+ remaining[0] + ";" + remaining[1] + ";" + unused[0] + ";" + unused[1]+ ";" + move[0]+ ";" + move[1]+ ";" + move[2];
    console.log(state);
    return state;
}

function checkMove(result)
{
    console.log(result.message);
    
    if (result.state == state)
	{
	    if ("message" in result)
		{
		}
	    else
		{  
			console.log(result.isValid);
           if (result.isValid == 'true'){
           	   
           	    $('.xcoin').each(function(i){
				if ($(this).attr('clicked')=='true'){
					thisspot.append($(this));
					thisspot.attr('value', 'x');
					var prev = $(this).attr('onboard');
                    $(this).attr('onboard', "'"+thisspot.attr('id')+"'");
					if (prev != 'null'){
						prev = prev.substring(1,prev.length-1);
						$('#'+prev).attr('value','empty');
						console.log($('#'+prev).attr('value'));
					}
					else{
						unused[1]--;
						console.log(unused[1]);
					}

					$(this).attr('clicked', 'false');
					$(this).css('borderColor','white');
					checkMill(result);
					
				}
			});
           }
		}
	}
}


function checkMill(result){
	if(result.formsMill=='true'){
		clickable = 'false';
		var set = result.set;
		var s = set.split("/");
		s.splice(s.length-1,1);
		console.log(s);
		for(var i=0;i<s.length;i++){
             var p = 'spot'+s[i];
             var tt = $("#"+p).find('.ycoin');
             tt.css('borderColor', 'yellow');
             tt.attr('remove', 'true');
	    }
	}
	else{
		doTurn();
	}
	
}

function doTurn(){
	clickable = 'false';
	turn = 0;
	$('#s1').text("COMPUTER'S TURN");
	state = encodeState();
    $.getJSON( "http://localhost:8080/move.html?" + state, doMove );
}

function doMove(result)
{
    console.log(result);
    if (result.state == state)
	{
	    if ("message" in result)
		{
		    $("#message").text(result.message);
		}
	    else
		{
		    state = result.spots + ";" + result.player;
		    //turn = result.player;
		    turn =1;
	
		    if (result.movefrom=='-1'){
		    	var n = $('#coins').children('.ycoin')[0];
		        unused[0]--;
		    }
		    else{
		    	var k = 'spot'+result.movefrom.toString();
		    	var n = $("#"+k).find('.ycoin');
		    	$("#"+k).attr('value','empty');
		    }
		    var j = 'spot'+result.moveto.toString();
		    $("#"+j).append(n);
		    $("#"+j).attr('value','y');
            if (result.movetaken != '-1'){
            	var p = 'spot'+result.movetaken.toString();
            	$("#"+p).find('.xcoin').remove();
            	$("#"+p).attr('value','empty');
            	remaining[1]--;
            }
            $("#s1").text("YOUR TURN");
            
		    clickable = 'true';
		    if (remaining[1]<3){
		    	$("#s1").text("Computer WINS!");
		    	clickable = 'false';
		    }

		}
	}
}

/*function setState(elt, code)
{
    if (code == '.')
	{
	    if($(elt).attr('value')=='x'){
	    	$(elt).find('.xcoin').remove();
	    }
	    else if ($(elt).attr('value')=='y'){
	    	$(elt).find('.ycoin').remove();
	    }

	    $(elt).attr('value', 'empty');
	    
	}
    else if (code == 'x')
	{
		if($(elt).attr('value')=='.'){
		   $('.xcoin').each(function(i){
			   if ($(this).attr('clicked')=='true'){
				  $(elt).append($(this));
				  $(this).attr('clicked', 'false');
				  $(this).css('borderColor','white');
			   }
		    });
	    }   
	    $(elt).attr('value', 'x');
	    
	}
    else
	{
		if($(elt).attr('value')=='.'){
		   $(elt).append($('.ycoin'));
	    }
	    $(elt).attr('value', 'y');
	 
	}
}
*/







