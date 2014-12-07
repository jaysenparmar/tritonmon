import ast

def main():
	print "HIHI"

	with open("moves.json", "r") as f:
		m = f.readlines()
	f.close()

	m = str(m[0])[1:-1]
	m = m.split("},")
	m[len(m)-1] = m[len(m)-1][:-1]
	for x in xrange(len(m)):
		m[x]+="}"

	tmp = "["

	arr = []

	for x in xrange(len(m)):
		each_move = ast.literal_eval(m[x])
		move_id = each_move["move_id"]
		name = str(each_move["name"])
		generation_id = str(each_move["generation_id"])
		flinch_chance = str(each_move["flinch_chance"])
		max_hits = str(each_move["max_hits"])
		drain = str(each_move["drain"])
		type_id = str(each_move["type_id"])
		stat_chance = str(each_move["stat_chance"])
		priority = str(each_move["priority"])
		ailment_chance = str(each_move["ailment_chance"])
		min_turns = str(each_move["min_turns"])
		crit_rate = str(each_move["crit_rate"])
		power = str(each_move["power"])
		min_hits = str(each_move["min_hits"])
		max_turns = str(each_move["max_turns"])
		damage_class_id = str(each_move["damage_class_id"])
		healing = str(each_move["healing"])
		pp = str(each_move["pp"])
		accuracy = str(each_move["accuracy"])
		move_meta_ailment_id = str(each_move["move_meta_ailment_id"])

		arr+=[name]

	with open("moveNames.txt", "w") as f:
		for ele in arr:
			f.write(ele+"\n")
	f.close()
main()